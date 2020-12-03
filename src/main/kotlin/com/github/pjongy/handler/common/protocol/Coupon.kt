package com.github.pjongy.handler.common.protocol

data class Coupon(
  val id: String,
  val name: String,
  val category: String,
  val totalAmount: Int,
  val discountAmount: Int?,
  val discountRate: Float?,
  val createdAt: String,
)
