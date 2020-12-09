package com.github.pjongy.handler.coupon

import com.github.pjongy.extension.toISO8601
import com.github.pjongy.handler.coupon.protocol.Coupon
import com.github.pjongy.handler.coupon.protocol.CouponWithUsageStatus
import com.github.pjongy.handler.coupon.protocol.GetCouponsWithUsageStatusRequest
import com.github.pjongy.handler.coupon.protocol.GetCouponsWithUsageStatusResponse
import com.github.pjongy.repository.CouponRepository
import com.google.gson.Gson
import java.time.Clock
import java.util.UUID
import javax.inject.Inject

class GetCouponsWithUsageStatusHandler @Inject constructor(
  private val clock: Clock,
  private val gson: Gson,
  private val couponRepository: CouponRepository,
) {

  suspend fun handle(request: GetCouponsWithUsageStatusRequest): String {
    val couponWithUsages = couponRepository.getCouponsWithUsageStatus(
      request.couponIds.map { UUID.fromString(it) }
    ).filterNotNull()

    val response = GetCouponsWithUsageStatusResponse(
      couponsWithUsageStatus = couponWithUsages.map {
        CouponWithUsageStatus(
          coupon = Coupon(
            id = it.coupon.id.toString(),
            name = it.coupon.name,
            category = it.coupon.category,
            totalAmount = it.coupon.totalAmount,
            discountAmount = it.coupon.discountAmount,
            discountRate = it.coupon.discountRate,
            createdAt = it.coupon.createdAt.toISO8601(clock.zone),
            expiredAt = it.coupon.createdAt.toISO8601(clock.zone),
            description = it.coupon.description,
            imageUrl = it.coupon.imageUrl,
          ),
          used = it.usedTotal.toInt(),
          unused = it.unusedTotal.toInt(),
          using = it.usingTotal.toInt(),
        )
      }
    )
    return gson.toJson(response)
  }
}
