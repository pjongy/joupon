package com.github.pjongy.repository

import com.github.pjongy.model.CouponEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

class CouponRepository @Inject constructor(
  private val db: Database,
  private val clock: Clock,
) {

  suspend fun createCoupon(
    name: String,
    category: String,
    totalAmount: Int,
    discountRate: Float?,
    discountAmount: Int?,
  ): CouponEntity {
    if (discountAmount == null && discountRate == null) {
      throw IllegalArgumentException("either discountAmount or discountRate should be passed")
    }
    return newSuspendedTransaction(db = db) {
      CouponEntity.new {
        this.name = name
        this.category = category
        this.totalAmount = totalAmount
        this.discountRate = discountRate
        this.discountAmount = discountAmount
        this.createdAt = Instant.now(clock)
      }
    }
  }
}
