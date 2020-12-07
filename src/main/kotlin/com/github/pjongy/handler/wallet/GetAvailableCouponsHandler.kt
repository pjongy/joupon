package com.github.pjongy.handler.wallet

import com.github.pjongy.extension.toISO8601
import com.github.pjongy.handler.wallet.protocol.Coupon
import com.github.pjongy.handler.wallet.protocol.GetAvailableCouponsRequest
import com.github.pjongy.handler.wallet.protocol.GetAvailableCouponsResponse
import com.github.pjongy.repository.CouponWalletRepository
import com.github.pjongy.repository.CouponWalletRepository.Companion.AVAILABLE_STRING_TO_STATUS
import com.google.gson.Gson
import java.time.Clock
import javax.inject.Inject

class GetAvailableCouponsHandler @Inject constructor(
  private val clock: Clock,
  private val gson: Gson,
  private val couponWalletRepository: CouponWalletRepository
) {

  suspend fun handle(request: GetAvailableCouponsRequest): String {
    val size = request.pageSize
    val start = (request.page * size).toLong()
    val (couponTotal, availableCoupons) = couponWalletRepository.findCouponsByUserId(
      ownerId = request.ownerId,
      status = request.status.mapNotNull { AVAILABLE_STRING_TO_STATUS[it] },
      start = start,
      size = size
    )

    val response = GetAvailableCouponsResponse(
      total = couponTotal,
      coupons = availableCoupons.map { coupon ->
        Coupon(
          id = coupon.id.toString(),
          name = coupon.name,
          category = coupon.category,
          discountAmount = coupon.discountAmount,
          discountRate = coupon.discountRate,
          expiredAt = coupon.expiredAt.toISO8601(clock.zone),
          description = coupon.description,
          imageUrl = coupon.imageUrl,
        )
      }
    )
    return gson.toJson(response)
  }
}
