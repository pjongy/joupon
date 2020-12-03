package com.github.pjongy.repository

import com.github.pjongy.model.CouponEntity
import com.github.pjongy.model.CouponWallet
import com.github.pjongy.model.CouponWalletEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Clock
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class CouponWalletRepository @Inject constructor(
  private val db: Database,
  private val clock: Clock,
) {

  suspend fun countByCouponId(id: UUID): Long {
    return newSuspendedTransaction(db = db) {
      CouponWalletEntity
        .find { CouponWallet.coupon eq id }
        .count()
    }
  }

  suspend fun create(coupon: CouponEntity, ownerId: String): CouponWalletEntity {
    return newSuspendedTransaction(db = db) {
      CouponWalletEntity.new {
        this.coupon = coupon
        this.ownerId = ownerId
        this.createdAt = Instant.now(clock)
      }
    }
  }

  suspend fun findCouponsByUserId(
    ownerId: String,
    start: Long,
    size: Int,
  ): Pair<Long, List<CouponEntity>> {
    val condition = CouponWallet.ownerId eq ownerId
    return newSuspendedTransaction(db = db) {
      val couponTotal = CouponWalletEntity
        .find { condition }
        .count()
      val coupons = CouponWalletEntity
        .find { condition }
        .limit(n = size, offset = start)
        .map {
          it.coupon
        }
      Pair(couponTotal, coupons)
    }
  }
}
