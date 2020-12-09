package com.github.pjongy.repository

import com.github.pjongy.extension.Union
import com.github.pjongy.model.Coupon
import com.github.pjongy.model.CouponRow
import com.github.pjongy.model.CouponWallet
import com.github.pjongy.model.CouponWalletStatus
import com.github.pjongy.model.CouponWithUsageStatus
import com.github.pjongy.model.wrapCouponRow
import org.jetbrains.exposed.sql.Coalesce
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.shortLiteral
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Clock
import java.time.Instant
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

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

  suspend fun getCouponsWithUsageStatus(
    couponIds: List<UUID>
  ): List<CouponWithUsageStatus?> {
    val walletCount = CouponWallet.id.count().alias("coupon_wallet__id__count")
    val walletStatus = Coalesce(CouponWallet.status, shortLiteral(0))
      .alias("coupon_wallet__status__coalesce")
    val (couponWalletResultRows, coupons) = newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      val queries = listOf(CouponWalletStatus.USED, CouponWalletStatus.UNUSED, CouponWalletStatus.USING).map {
        CouponWallet.slice(
          walletCount,
          CouponWallet.couponId,
          walletStatus
        ).select {
          CouponWallet.couponId inList couponIds and
            (CouponWallet.status eq it)
        }.groupBy(CouponWallet.couponId)
      }
      Union(queries).toList() to Coupon.select { Coupon.id inList couponIds }.map { wrapCouponRow(it) }
      // NOTE(pjongy): exposed does not support join with virtual table
    }
    val couponUsageStatus = couponWalletResultRows
      .groupBy(
        { it[CouponWallet.couponId] },
        { it[walletStatus] to it[walletCount] },
      )
    val couponMap = coupons.associateBy { it.id }

    return couponUsageStatus.mapNotNull { (uuid, statusWithCount) ->
      val statusCountMap = statusWithCount.associateBy({ it.first.toString().toInt() }, { it.second })
      couponMap[uuid]?.let {
        CouponWithUsageStatus(
          usedTotal = statusCountMap[CouponWalletStatus.USED.ordinal] ?: 0,
          unusedTotal = statusCountMap[CouponWalletStatus.UNUSED.ordinal] ?: 0,
          usingTotal = statusCountMap[CouponWalletStatus.USING.ordinal] ?: 0,
          coupon = it
        )
      }
    }
  }
}
