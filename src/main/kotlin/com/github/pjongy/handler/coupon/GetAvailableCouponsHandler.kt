package com.github.pjongy.handler.coupon

import javax.inject.Inject

data class GetAvailableCouponsRequest(
  val page: Int,
  val pageSize: Int
)

class GetAvailableCouponsHandler @Inject constructor() {

  suspend fun handle(request: GetAvailableCouponsRequest): String {
    TODO("Not yet implemented")
  }
}
