package com.github.pjongy.handler.wallet.protocol

data class Coupon(
  val id: String,
  val name: String,
  val category: String,
  val discountAmount: Int?,
  val discountRate: Float?,
  val expiredAt: String,
  val description: String,
  val imageUrl: String,
)
