package com.github.pjongy.handler.wallet.protocol

data class IssueCouponRequestBody(
  val properties: List<ConditionProperty>,
)

data class IssueCouponRequest(
  val ownerId: String,
  val properties: List<ConditionProperty>,
  val couponId: String,
)

data class IssueCouponResponse(
  val ownerId: String,
  val coupon: Coupon,
)
