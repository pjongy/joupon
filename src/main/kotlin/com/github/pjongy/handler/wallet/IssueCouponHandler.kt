package com.github.pjongy.handler.wallet

import com.github.pjongy.exception.Duplicated
import com.github.pjongy.exception.OutOfAvailableData
import com.github.pjongy.handler.wallet.protocol.Coupon
import com.github.pjongy.handler.wallet.protocol.IssueCouponRequest
import com.github.pjongy.handler.wallet.protocol.IssueCouponResponse
import com.github.pjongy.repository.CouponRepository
import com.github.pjongy.repository.CouponWalletRepository
import com.google.gson.Gson
import java.time.Clock
import java.util.UUID
import javax.inject.Inject

class IssueCouponHandler @Inject constructor(
  private val clock: Clock,
  private val gson: Gson,
  private val couponRepository: CouponRepository,
  private val couponWalletRepository: CouponWalletRepository,
) {

  // NOTE(pjongy): It could blows timing issue because of check-and-insert is separated (It should be atomic)
  suspend fun handle(request: IssueCouponRequest): String {
    val (couponTotal, _) = couponWalletRepository.findCouponsByUserId(
      ownerId = request.ownerId, start = 0, size = 0
    )
    if (couponTotal > 0) {
      throw Duplicated("already issued coupon")
    }
    val couponId = UUID.fromString(request.couponId)
    val coupon = couponRepository.findById(couponId)
    val currentCouponTotal = couponWalletRepository.countByCouponId(id = couponId)

    if (coupon.totalAmount < currentCouponTotal) {
      throw OutOfAvailableData("available count: ${coupon.totalAmount}")
    }

    val couponWallet = couponWalletRepository.create(coupon = coupon, ownerId = request.ownerId)
    val response = IssueCouponResponse(
      ownerId = couponWallet.ownerId,
      coupon = Coupon(
        id = coupon.id.toString(),
        name = coupon.name,
        category = coupon.category,
        discountAmount = coupon.discountAmount,
        discountRate = coupon.discountRate,
      )
    )
    return gson.toJson(response)
  }
}
