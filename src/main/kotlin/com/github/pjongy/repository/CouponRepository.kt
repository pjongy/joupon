package com.github.pjongy.repository

import com.github.pjongy.model.Coupon
import com.github.pjongy.model.CouponEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
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

  suspend fun findById(id: UUID): CouponEntity {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      CouponEntity.find { Coupon.id eq id }.first()
    }
  }

  suspend fun fetchCoupons(
    start: Long,
    size: Int,
  ): Pair<Long, List<CouponEntity>> {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      val couponTotal = CouponEntity.count()
      val coupons = CouponEntity
        .all()
        .limit(n = size, offset = start)
        .toList()
      Pair(couponTotal, coupons)
    }
  }

  suspend fun createCoupon(
    name: String,
    category: String,
    totalAmount: Int,
    discountRate: Float?,
    discountAmount: Int?,
    expiredAt: ZonedDateTime,
  ): CouponEntity {
    if (discountAmount == null && discountRate == null) {
      throw IllegalArgumentException("either discountAmount or discountRate should be passed")
    }
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      CouponEntity.new {
        this.name = name
        this.category = category
        this.totalAmount = totalAmount
        this.discountRate = discountRate
        this.discountAmount = discountAmount
        this.createdAt = Instant.now(clock)
        this.expiredAt = expiredAt.toInstant()
      }
    }
  }
}
