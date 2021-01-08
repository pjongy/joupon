package com.github.pjongy.handler.wallet

import com.github.pjongy.exception.InvalidParameter
import com.github.pjongy.exception.NotFound
import com.github.pjongy.exception.UnAvailableData
import com.github.pjongy.handler.wallet.protocol.ChangeCouponStatusRequest
import com.github.pjongy.handler.wallet.protocol.ChangeCouponStatusResponse
import com.github.pjongy.model.CouponStatus
import com.github.pjongy.repository.CouponRepository
import com.github.pjongy.repository.CouponWalletRepository
import com.github.pjongy.repository.CouponWalletRepository.Companion.AVAILABLE_STRING_TO_STATUS
import com.google.gson.Gson
import java.time.Clock
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class ChangeCouponStatusHandler @Inject constructor(
  private val clock: Clock,
  private val gson: Gson,
  private val couponRepository: CouponRepository,
  private val couponWalletRepository: CouponWalletRepository,
) {

  suspend fun handle(request: ChangeCouponStatusRequest): String {
    if (!AVAILABLE_STRING_TO_STATUS.containsKey(request.status)) {
      throw InvalidParameter("invalid status: ${request.status}")
    }

    val couponWallet = try {
      couponWalletRepository.findByOwnerIdAndCouponId(
        ownerId = request.ownerId,
        couponId = UUID.fromString(request.couponId),
      )
    } catch (e: NoSuchElementException) {
      throw NotFound("there is no coupon ${request.couponId} for ${request.ownerId}")
    }

    val currentTime = Instant.now(clock)
    val coupon = try {
      couponRepository.findById(UUID.fromString(request.couponId))
    } catch (e: NoSuchElementException) {
      throw throw NotFound("invalid coupon")
    }
    if (coupon.expiredAt.isBefore(currentTime)) {
      throw UnAvailableData("coupon expiation over")
    }
    if (coupon.status != CouponStatus.NORMAL) {
      throw UnAvailableData("invalid coupon (it might be deleted)")
    }

    AVAILABLE_STRING_TO_STATUS[request.status]?.let {
      couponWalletRepository.updateCouponWalletStatus(request.ownerId, coupon.id, it)
    } ?: throw InvalidParameter("invalid status: ${request.status}")

    val response = ChangeCouponStatusResponse(
      ownerId = couponWallet.ownerId,
      couponId = request.couponId,
      status = request.status,
    )
    return gson.toJson(response)
  }
}
