package com.github.pjongy.repository

import com.github.pjongy.model.Coupon
import com.github.pjongy.model.CouponEntity
import com.github.pjongy.model.CouponStatus
import com.github.pjongy.model.CouponWallet
import com.github.pjongy.model.CouponWalletEntity
import com.github.pjongy.model.CouponWalletStatus
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
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
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      val query = (Coupon innerJoin CouponWallet)
        .select {
          Coupon.status eq CouponStatus.NORMAL and
            (CouponWallet.ownerId eq ownerId) and
            (Coupon.expiredAt greaterEq Instant.now(clock))
        }
      val couponTotal = query.count()
      val coupons = CouponEntity.wrapRows(query.limit(size, start)).toList()
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

  suspend fun findByOwnerIdAndCouponId(
    ownerId: String,
    couponId: UUID,
  ): CouponWalletEntity? {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      try {
        CouponWalletEntity.find {
          CouponWallet.coupon eq couponId and (CouponWallet.ownerId eq ownerId)
        }.first()
      } catch (e: NoSuchElementException) {
        null
      }
    }
  }

  suspend fun updateCouponWalletStatus(
    couponWalletEntity: CouponWalletEntity,
    couponWalletStatus: CouponWalletStatus
  ) {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      couponWalletEntity.status = couponWalletStatus
    }
  }
}
