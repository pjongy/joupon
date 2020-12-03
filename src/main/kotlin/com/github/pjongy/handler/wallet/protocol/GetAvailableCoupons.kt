package com.github.pjongy.handler.wallet.protocol

import com.github.pjongy.handler.common.protocol.Coupon

data class GetAvailableCouponsRequest(
  val ownerId: String,
  val page: Int,
  val pageSize: Int,
)

data class GetAvailableCouponsResponse(
  val coupons: List<Coupon>,
  val total: Long,
)
