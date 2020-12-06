package com.github.pjongy.handler.coupon.protocol

data class CouponWithIssuedCount(
  val coupon: Coupon,
  val issued: Int,
)

data class GetCouponsWithIssuedCountRequest(
  val couponIds: List<String>,
)

data class GetCouponsWithIssuedCountResponse(
  val couponsWithIssuedCount: List<CouponWithIssuedCount>
)
