package com.github.pjongy.common

import java.util.UUID

data class Coupon(
  val id: UUID,
  val name: String,
  val category: String,
  val totalAmount: Int,
  val discountAmount: Int,
  val discountRate: Float,
  val createdAt: String,
)
