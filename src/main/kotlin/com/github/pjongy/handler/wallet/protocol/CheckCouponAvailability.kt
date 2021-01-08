package com.github.pjongy.handler.wallet.protocol

data class CheckCouponAvailabilityRequestBody(
  val properties: List<ConditionProperty>,
)

data class CheckCouponAvailabilityRequest(
  val ownerId: String,
  val properties: List<ConditionProperty>,
  val couponId: String,
)

data class CheckCouponAvailabilityResponse(
  val ownerId: String,
  val coupon: Coupon,
)
