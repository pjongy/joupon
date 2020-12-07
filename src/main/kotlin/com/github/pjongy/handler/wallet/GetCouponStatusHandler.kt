package com.github.pjongy.handler.wallet

import com.github.pjongy.exception.NotFound
import com.github.pjongy.handler.wallet.protocol.GetCouponStatusRequest
import com.github.pjongy.handler.wallet.protocol.GetCouponStatusResponse
import com.github.pjongy.repository.CouponWalletRepository
import com.github.pjongy.repository.CouponWalletRepository.Companion.AVAILABLE_STATUS_TO_STRING
import com.google.gson.Gson
import java.util.UUID
import javax.inject.Inject

class GetCouponStatusHandler @Inject constructor(
  private val gson: Gson,
  private val couponWalletRepository: CouponWalletRepository,
) {

  suspend fun handle(request: GetCouponStatusRequest): String {
    val couponWallet = couponWalletRepository.findByOwnerIdAndCouponId(
      ownerId = request.ownerId, couponId = UUID.fromString(request.couponId)
    ) ?: throw NotFound("there are no coupon ${request.couponId} for ${request.ownerId}")

    val response = GetCouponStatusResponse(
      ownerId = couponWallet.ownerId,
      couponId = request.couponId,
      status = AVAILABLE_STATUS_TO_STRING[couponWallet.status] ?: "UNKNOWN"
    )
    return gson.toJson(response)
  }
}
