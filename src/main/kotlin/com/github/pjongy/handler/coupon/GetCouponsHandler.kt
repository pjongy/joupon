package com.github.pjongy.handler.coupon

import javax.inject.Inject

data class GetCouponsRequest(
  val page: Int,
  val pageSize: Int
)

class GetCouponsHandler @Inject constructor() {

  suspend fun handle(request: GetCouponsRequest): String {
    TODO("Not yet implemented")
  }
}
