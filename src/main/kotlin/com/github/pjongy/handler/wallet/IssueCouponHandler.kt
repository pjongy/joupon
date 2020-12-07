package com.github.pjongy.handler.wallet

import com.github.pjongy.exception.Duplicated
import com.github.pjongy.exception.UnAvailableData
import com.github.pjongy.extension.toISO8601
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
    val isAlreadyIssued = couponWalletRepository.checkExistenceByOwnerIdAndCouponId(
      ownerId = request.ownerId, couponId = UUID.fromString(request.couponId)
    )
    if (isAlreadyIssued) {
      throw Duplicated("already issued coupon")
    }
    val couponId = UUID.fromString(request.couponId)
    val coupon = couponRepository.findById(couponId)
    val currentCouponTotal = couponWalletRepository.countByCouponId(id = couponId)

    if (coupon.totalAmount <= currentCouponTotal) {
      throw UnAvailableData("available count: ${coupon.totalAmount}")
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
        expiredAt = coupon.expiredAt.toISO8601(clock.zone),
        description = coupon.description,
        imageUrl = coupon.imageUrl,
      )
    )
    return gson.toJson(response)
  }
}
