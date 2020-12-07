package com.github.pjongy.handler.wallet.protocol

data class GetCouponStatusRequest(
  val ownerId: String,
  val couponId: String,
)

data class GetCouponStatusResponse(
  val ownerId: String,
  val couponId: String,
  val status: String,
)
