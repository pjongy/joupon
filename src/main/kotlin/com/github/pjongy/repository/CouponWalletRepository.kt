package com.github.pjongy.repository

import com.github.pjongy.model.Coupon
import com.github.pjongy.model.CouponRow
import com.github.pjongy.model.CouponStatus
import com.github.pjongy.model.CouponWallet
import com.github.pjongy.model.CouponWalletRow
import com.github.pjongy.model.CouponWalletStatus
import com.github.pjongy.model.wrapCouponRow
import com.github.pjongy.model.wrapCouponWalletRow
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.time.Clock
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class CouponWalletRepository @Inject constructor(
  private val db: Database,
  private val clock: Clock,
) {
  companion object {
    val AVAILABLE_STRING_TO_STATUS = mapOf(
      "UNUSED" to CouponWalletStatus.UNUSED,
      "USING" to CouponWalletStatus.USING,
      "USED" to CouponWalletStatus.USED,
    )
    val AVAILABLE_STATUS_TO_STRING = mapOf(
      CouponWalletStatus.UNUSED to "UNUSED",
      CouponWalletStatus.USING to "USING",
      CouponWalletStatus.USED to "USED",
    )
  }

  suspend fun countByCouponId(id: UUID): Long {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      CouponWallet.select { CouponWallet.couponId eq id }.count()
    }
  }

  suspend fun create(couponId: UUID, ownerId: String): CouponWalletRow? {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      val insertQuery = CouponWallet.insert {
        it[this.couponId] = couponId
        it[this.ownerId] = ownerId
        it[this.createdAt] = Instant.now(clock)
      }
      insertQuery.resultedValues?.let {
        wrapCouponWalletRow(it.first())
      }
    }
  }

  suspend fun findCouponsByUserId(
    ownerId: String,
    status: List<CouponWalletStatus>,
    start: Long,
    size: Int,
  ): Pair<Long, List<CouponRow>> {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      val query = Coupon.join(
        CouponWallet,
        JoinType.INNER,
        additionalConstraint = {
          Coupon.id eq CouponWallet.couponId
        }
      ).select {
        Coupon.status eq CouponStatus.NORMAL and
          (CouponWallet.ownerId eq ownerId) and
          (Coupon.expiredAt greaterEq Instant.now(clock)) and
          (CouponWallet.status inList status)
      }
      val couponTotal = query.count()
      val coupons = query.limit(size, start).map { wrapCouponRow(it) }
      Pair(couponTotal, coupons)
    }
  }

  suspend fun checkExistenceByOwnerIdAndCouponId(
    ownerId: String,
    couponId: UUID,
  ): Boolean {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      CouponWallet.select {
        CouponWallet.couponId eq couponId and
          (CouponWallet.ownerId eq ownerId)
      }.count() > 0
    }
  }

  suspend fun findByOwnerIdAndCouponId(
    ownerId: String,
    couponId: UUID,
  ): CouponWalletRow {
    return wrapCouponWalletRow(
      newSuspendedTransaction(db = db) {
        addLogger(Slf4jSqlDebugLogger)
        CouponWallet.select {
          CouponWallet.couponId eq couponId and
            (CouponWallet.ownerId eq ownerId)
        }.first()
      }
    )
  }

  suspend fun updateCouponWalletStatus(
    ownerId: String,
    couponId: UUID,
    couponWalletStatus: CouponWalletStatus
  ) {
    return newSuspendedTransaction(db = db) {
      addLogger(Slf4jSqlDebugLogger)
      CouponWallet.update({
        CouponWallet.couponId eq couponId and
          (CouponWallet.ownerId eq ownerId)
      }) {
        it[this.status] = couponWalletStatus
      }
    }
  }
}
