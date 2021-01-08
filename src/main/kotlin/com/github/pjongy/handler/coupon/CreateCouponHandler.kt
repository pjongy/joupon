package com.github.pjongy.handler.coupon

import com.github.pjongy.exception.HandlerException
import com.github.pjongy.exception.InvalidParameter
import com.github.pjongy.extension.toISO8601
import com.github.pjongy.handler.coupon.extension.isValid
import com.github.pjongy.handler.coupon.protocol.Condition
import com.github.pjongy.handler.coupon.protocol.CreateCouponRequest
import com.github.pjongy.handler.coupon.protocol.CreateCouponResponse
import com.github.pjongy.repository.CouponRepository
import com.google.gson.Gson
import java.time.Clock
import java.time.ZonedDateTime
import javax.inject.Inject

class CreateCouponHandler @Inject constructor(
  private val clock: Clock,
  private val gson: Gson,
  private val couponRepository: CouponRepository,
) {

  suspend fun handle(request: CreateCouponRequest): String {
    if (!request.issuingCondition.isValid() || !request.usingCondition.isValid()) {
      throw InvalidParameter("wrong coupon condition structure")
    }
    val coupon = couponRepository.createCoupon(
      name = request.name,
      category = request.category,
      totalAmount = request.totalAmount,
      discountAmount = request.discountAmount,
      discountRate = request.discountRate,
      expiredAt = ZonedDateTime.parse(request.expiredAt),
      description = request.description,
      imageUrl = request.imageUrl,
      issuingCondition = gson.toJson(request.issuingCondition),
      usingCondition = gson.toJson(request.usingCondition),
    ) ?: throw HandlerException("coupon row insertion failed")

    val response = CreateCouponResponse(
      id = coupon.id.toString(),
      name = coupon.name,
      category = coupon.category,
      totalAmount = coupon.totalAmount,
      discountAmount = coupon.discountAmount,
      discountRate = coupon.discountRate,
      createdAt = coupon.createdAt.toISO8601(clock.zone),
      expiredAt = coupon.createdAt.toISO8601(clock.zone),
      description = coupon.description,
      imageUrl = coupon.imageUrl,
      issuingCondition = gson.fromJson(coupon.issuingCondition, Condition::class.java),
      usingCondition = gson.fromJson(coupon.usingCondition, Condition::class.java),
    )
    return gson.toJson(response)
  }
}
