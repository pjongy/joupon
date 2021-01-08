package com.github.pjongy.handler.coupon.protocol

data class Coupon(
  val id: String,
  val name: String,
  val category: String,
  val totalAmount: Int,
  val discountAmount: Int?,
  val discountRate: Float?,
  val createdAt: String,
  val expiredAt: String,
  val description: String,
  val imageUrl: String,
  val issuingCondition: Condition,
  val usingCondition: Condition,
)
