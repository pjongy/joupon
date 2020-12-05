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
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and

class CouponWalletRepository @Inject constructor(
  private val db: Database,
  private val clock: Clock,
) {

  suspend fun countByCouponId(id: UUID): Long {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      CouponWalletEntity
        .find { CouponWallet.coupon eq id }
        .count()
    }
  }

  suspend fun create(coupon: CouponEntity, ownerId: String): CouponWalletEntity {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
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
      addLogger(Slf4jSqlDebugLogger)
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

  suspend fun checkExistenceByOwnerIdAndCouponId(
    ownerId: String,
    couponId: UUID,
  ): Boolean {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      CouponWalletEntity.find {
        CouponWallet.coupon eq couponId and (CouponWallet.ownerId eq ownerId)
      }.count() > 0
    }
  }
}
