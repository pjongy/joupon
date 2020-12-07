package com.github.pjongy.handler.wallet.protocol

data class GetAvailableCouponsRequest(
  val ownerId: String,
  val page: Int,
  val pageSize: Int,
  val status: List<String>,
)

data class GetAvailableCouponsResponse(
  val coupons: List<Coupon>,
  val total: Long,
)
