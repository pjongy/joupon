package com.github.pjongy.handler.wallet

import com.github.pjongy.exception.HandlerException
import com.github.pjongy.exception.NotFound
import com.github.pjongy.exception.UnAvailableData
import com.github.pjongy.extension.toISO8601
import com.github.pjongy.handler.wallet.extension.isAvailable
import com.github.pjongy.handler.wallet.protocol.CheckCouponAvailabilityRequest
import com.github.pjongy.handler.wallet.protocol.CheckCouponAvailabilityResponse
import com.github.pjongy.handler.wallet.protocol.Condition
import com.github.pjongy.handler.wallet.protocol.Coupon
import com.github.pjongy.model.CouponStatus
import com.github.pjongy.model.CouponWalletStatus
import com.github.pjongy.repository.CouponRepository
import com.github.pjongy.repository.CouponWalletRepository
import com.google.gson.Gson
import java.time.Clock
import java.util.UUID
import javax.inject.Inject

class CheckCouponAvailabilityHandler @Inject constructor(
  private val clock: Clock,
  private val gson: Gson,
  private val couponRepository: CouponRepository,
  private val couponWalletRepository: CouponWalletRepository,
) {

  suspend fun handle(request: CheckCouponAvailabilityRequest): String {
    val couponId = UUID.fromString(request.couponId)
    val coupon = try {
      couponRepository.findById(couponId)
    } catch (e: NoSuchElementException) {
      throw throw NotFound("invalid coupon")
    }
    val condition = gson.fromJson(coupon.usingCondition, Condition::class.java)

    if (coupon.status == CouponStatus.DELETED) {
      throw UnAvailableData("coupon is expired")
    }

    if (!condition.isAvailable(request.properties)) {
      throw UnAvailableData("condition for coupon is not satisfied")
    }
    val couponWallet = try {
      couponWalletRepository.findByOwnerIdAndCouponId(
        couponId = couponId,
        ownerId = request.ownerId,
      )
    } catch (e: NoSuchElementException) {
      throw NotFound("coupon not found in wallet")
    }

    if (couponWallet.status != CouponWalletStatus.UNUSED) {
      throw HandlerException("coupon status is not UNUSED")
    }

    val response = CheckCouponAvailabilityResponse(
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
