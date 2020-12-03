package com.github.pjongy.handler.wallet.protocol

import com.github.pjongy.handler.common.protocol.Coupon

data class IssueCouponRequest(
  val ownerId: String,
  val couponId: String,
)

data class IssueCouponResponse(
  val ownerId: String,
  val coupon: Coupon,
)