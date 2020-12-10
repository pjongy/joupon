package com.github.pjongy.repository

import com.github.pjongy.model.Coupon
import com.github.pjongy.model.CouponRow
import com.github.pjongy.model.CouponWallet
import com.github.pjongy.model.CouponWithIssuedCount
import com.github.pjongy.model.wrapCouponRow
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Clock
import java.time.Instant
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class CouponRepository @Inject constructor(
  private val db: Database,
  private val clock: Clock,
) {

  suspend fun findById(id: UUID): CouponRow {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      val resultRow = Coupon.select { Coupon.id eq id }.first()
      wrapCouponRow(resultRow)
    }
  }

  suspend fun fetchCoupons(
    start: Long,
    size: Int,
  ): Pair<Long, List<CouponRow>> {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      val query = Coupon.selectAll()
      val couponTotal = query.count()
      val coupons = query.limit(n = size, offset = start)
      Pair(couponTotal, coupons.map { wrapCouponRow(it) })
    }
  }

  suspend fun createCoupon(
    name: String,
    category: String,
    totalAmount: Int,
    discountRate: Float?,
    discountAmount: Int?,
    description: String,
    imageUrl: String,
    expiredAt: ZonedDateTime,
  ): CouponRow? {
    if (discountAmount == null && discountRate == null) {
      throw IllegalArgumentException("either discountAmount or discountRate should be passed")
    }
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      val insertQuery = Coupon.insert {
        it[this.name] = name
        it[this.category] = category
        it[this.totalAmount] = totalAmount
        it[this.discountRate] = discountRate
        it[this.discountAmount] = discountAmount
        it[this.description] = description
        it[this.imageUrl] = imageUrl
        it[this.createdAt] = Instant.now(clock)
        it[this.expiredAt] = expiredAt.toInstant()
      }
      insertQuery.resultedValues?.let {
        wrapCouponRow(it.first())
      }
    }
  }

  suspend fun getCouponsWithIssuedCount(
    couponIds: List<UUID>
  ): List<CouponWithIssuedCount> {
    val issuedCount = CouponWallet.id.count().alias("coupon_wallet__total__grouped_by__coupon")
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      val query = Coupon.join(
        CouponWallet,
        JoinType.LEFT,
        additionalConstraint = {
          Coupon.id eq CouponWallet.couponId
        }
      ).slice(
        Coupon.id,
        Coupon.name,
        Coupon.category,
        Coupon.totalAmount,
        Coupon.discountAmount,
        Coupon.discountRate,
        Coupon.description,
        Coupon.imageUrl,
        Coupon.createdAt,
        Coupon.expiredAt,
        Coupon.status,
        issuedCount,
      ).select {
        Coupon.id inList couponIds
      }.groupBy(Coupon.id)
      query.map {
        CouponWithIssuedCount(
          issuedTotal = it[issuedCount],
          coupon = wrapCouponRow(it),
        )
      }
    }
  }
}
