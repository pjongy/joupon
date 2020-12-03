package com.github.pjongy.handler.coupon

import com.github.pjongy.handler.coupon.protocol.GetCouponsRequest
import javax.inject.Inject

class GetCouponsHandler @Inject constructor() {

  suspend fun handle(request: GetCouponsRequest): String {
    TODO("Not yet implemented")
  }
}
