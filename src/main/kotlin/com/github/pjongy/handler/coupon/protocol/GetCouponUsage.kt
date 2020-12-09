package com.github.pjongy.handler.coupon.protocol

data class CouponWithUsageStatus(
  val coupon: Coupon,
  val using: Int,
  val unused: Int,
  val used: Int,
)

data class GetCouponsWithUsageStatusRequest(
  val couponIds: List<String>,
)

data class GetCouponsWithUsageStatusResponse(
  val couponsWithUsageStatus: List<CouponWithUsageStatus>
)
