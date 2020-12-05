package com.github.pjongy.handler.coupon.protocol

data class GetCouponsRequest(
  val page: Int,
  val pageSize: Int
)

data class GetCouponsResponse(
  val coupons: List<Coupon>,
  val total: Long,
)
