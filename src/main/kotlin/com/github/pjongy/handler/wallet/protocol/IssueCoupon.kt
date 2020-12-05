package com.github.pjongy.handler.wallet.protocol

data class IssueCouponRequest(
  val ownerId: String,
  val couponId: String,
)

data class IssueCouponResponse(
  val ownerId: String,
  val coupon: Coupon,
)
