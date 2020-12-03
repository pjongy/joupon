package com.github.pjongy.handler.coupon

import com.github.pjongy.extension.toISO8601
import com.github.pjongy.handler.common.protocol.Coupon
import com.github.pjongy.handler.coupon.protocol.GetCouponsRequest
import com.github.pjongy.handler.coupon.protocol.GetCouponsResponse
import com.github.pjongy.repository.CouponRepository
import com.google.gson.Gson
import java.time.Clock
import javax.inject.Inject

class GetCouponsHandler @Inject constructor(
  private val clock: Clock,
  private val gson: Gson,
  private val couponRepository: CouponRepository,
) {

  suspend fun handle(request: GetCouponsRequest): String {
    val size = request.pageSize
    val start = (request.page * size).toLong()
    val (couponTotal, coupons) = couponRepository.fetchCoupons(
      start = start,
      size = size
    )

    val response = GetCouponsResponse(
      total = couponTotal,
      coupons = coupons.map { coupon ->
        Coupon(
          id = coupon.id.toString(),
          name = coupon.name,
          category = coupon.category,
          totalAmount = coupon.totalAmount,
          discountAmount = coupon.discountAmount,
          discountRate = coupon.discountRate,
          createdAt = coupon.createdAt.toISO8601(clock.zone)
        )
      }
    )
    return gson.toJson(response)
  }
}
