package com.github.pjongy.handler.coupon.protocol

data class CreateCouponRequestBody(
  val name: String,
  val category: String,
  val totalAmount: Int,
  val discountRate: Float?,
  val discountAmount: Int?,
  val expiredAt: String,
  val description: String,
  val imageUrl: String,
)

data class CreateCouponRequest(
  val name: String,
  val category: String,
  val totalAmount: Int,
  val discountRate: Float?,
  val discountAmount: Int?,
  val expiredAt: String,
  val description: String,
  val imageUrl: String,
)

data class CreateCouponResponse(
  val id: String,
  val name: String,
  val category: String,
  val totalAmount: Int,
  val discountRate: Float?,
  val discountAmount: Int?,
  val createdAt: String,
  val expiredAt: String,
  val description: String,
  val imageUrl: String,
)
