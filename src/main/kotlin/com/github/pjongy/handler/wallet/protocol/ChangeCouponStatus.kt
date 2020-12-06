package com.github.pjongy.handler.wallet.protocol

data class ChangeCouponStatusRequest(
  val ownerId: String,
  val couponId: String,
  val status: String,
)

data class ChangeCouponStatusResponse(
  val ownerId: String,
  val couponId: String,
  val status: String,
)
