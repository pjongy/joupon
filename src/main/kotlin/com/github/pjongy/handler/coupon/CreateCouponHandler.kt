package com.github.pjongy.handler.coupon

import com.github.pjongy.handler.coupon.protocol.CreateCouponRequest
import com.github.pjongy.handler.coupon.protocol.CreateCouponResponse
import com.github.pjongy.repository.CouponRepository
import com.google.gson.Gson
import java.time.Clock
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CreateCouponHandler @Inject constructor(
  private val clock: Clock,
  private val gson: Gson,
  private val couponRepository: CouponRepository,
) {

  suspend fun handle(request: CreateCouponRequest): String {
    val coupon = couponRepository.createCoupon(
      name = request.name,
      category = request.category,
      totalAmount = request.totalAmount,
      discountAmount = request.discountAmount,
      discountRate = request.discountRate,
      expiredAt = ZonedDateTime.parse(request.expiredAt),
      description = request.description,
      imageUrl = request.imageUrl,
    )

    val response = CreateCouponResponse(
      id = coupon.id.toString(),
      name = coupon.name,
      category = coupon.category,
      totalAmount = coupon.totalAmount,
      discountAmount = coupon.discountAmount,
      discountRate = coupon.discountRate,
      createdAt = ZonedDateTime
        .ofInstant(coupon.createdAt, clock.zone)
        .format(DateTimeFormatter.ISO_DATE_TIME),
      description = coupon.description,
      imageUrl = coupon.imageUrl,
    )
    return gson.toJson(response)
  }
}
