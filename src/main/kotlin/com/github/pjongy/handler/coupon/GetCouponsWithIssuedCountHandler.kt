package com.github.pjongy.handler.coupon

import com.github.pjongy.extension.toISO8601
import com.github.pjongy.handler.coupon.protocol.Coupon
import com.github.pjongy.handler.coupon.protocol.CouponWithIssuedCount
import com.github.pjongy.handler.coupon.protocol.GetCouponsWithIssuedCountRequest
import com.github.pjongy.handler.coupon.protocol.GetCouponsWithIssuedCountResponse
import com.github.pjongy.repository.CouponRepository
import com.google.gson.Gson
import java.time.Clock
import java.util.UUID
import javax.inject.Inject

class GetCouponsWithIssuedCountHandler @Inject constructor(
  private val clock: Clock,
  private val gson: Gson,
  private val couponRepository: CouponRepository,
) {

  suspend fun handle(request: GetCouponsWithIssuedCountRequest): String {
    val couponWithUsages = couponRepository.getCouponsWithIssuedCount(
      request.couponIds.map { UUID.fromString(it) }
    )
    val response = GetCouponsWithIssuedCountResponse(
      couponsWithIssuedCount = couponWithUsages.map {
        CouponWithIssuedCount(
          coupon = Coupon(
            id = it.coupon.id.toString(),
            name = it.coupon.name,
            category = it.coupon.category,
            totalAmount = it.coupon.totalAmount,
            discountAmount = it.coupon.discountAmount,
            discountRate = it.coupon.discountRate,
            createdAt = it.coupon.createdAt.toISO8601(clock.zone),
            description = it.coupon.description,
            imageUrl = it.coupon.imageUrl,
          ),
          issued = it.issuedTotal.toInt(),
        )
      }
    )
    return gson.toJson(response)
  }
}
