package com.github.pjongy.handler.wallet

import com.github.pjongy.extension.toISO8601
import com.github.pjongy.repository.CouponWalletRepository
import com.google.gson.Gson
import java.time.Clock
import javax.inject.Inject

data class GetAvailableCouponsRequest(
  val ownerId: String,
  val page: Int,
  val pageSize: Int,
)

data class Coupon(
  val id: String,
  val name: String,
  val category: String,
  val totalAmount: Int,
  val discountAmount: Int?,
  val discountRate: Float?,
  val createdAt: String,
)

data class GetAvailableCouponsResponse(
  val coupons: List<Coupon>,
  val total: Long,
)

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
