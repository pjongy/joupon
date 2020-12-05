package com.github.pjongy.handler.coupon.protocol

data class CreateCouponRequest(
  val name: String,
  val category: String,
  val totalAmount: Int,
  val discountRate: Float?,
  val discountAmount: Int?,
  val expiredAt: String,
)

data class CreateCouponResponse(
  val id: String,
  val name: String,
  val category: String,
  val totalAmount: Int,
  val discountRate: Float?,
  val discountAmount: Int?,
  val createdAt: String,
)
