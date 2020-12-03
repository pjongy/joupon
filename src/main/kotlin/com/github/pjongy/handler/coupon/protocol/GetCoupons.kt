package com.github.pjongy.handler.coupon.protocol

import com.github.pjongy.handler.common.protocol.Coupon

data class GetCouponsRequest(
  val page: Int,
  val pageSize: Int
)

data class GetCouponsResponse(
  val coupons: List<Coupon>,
  val total: Long,
)
