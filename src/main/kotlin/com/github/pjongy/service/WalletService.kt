package com.github.pjongy.service

import com.github.pjongy.exception.InvalidParameter
import com.github.pjongy.handler.wallet.ChangeCouponStatusHandler
import com.github.pjongy.handler.wallet.GetAvailableCouponsHandler
import com.github.pjongy.handler.wallet.GetCouponStatusHandler
import com.github.pjongy.handler.wallet.IssueCouponHandler
import com.github.pjongy.handler.wallet.protocol.ChangeCouponStatusRequest
import com.github.pjongy.handler.wallet.protocol.GetAvailableCouponsRequest
import com.github.pjongy.handler.wallet.protocol.GetCouponStatusRequest
import com.github.pjongy.handler.wallet.protocol.IssueCouponRequest
import com.github.pjongy.service.handler.InternalAuthHandler
import com.github.pjongy.service.handler.coroutineHandler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import javax.inject.Inject

class WalletService @Inject constructor(
  private val vertx: Vertx,
  private val internalAuthHandler: InternalAuthHandler,
  private val getAvailableCouponsHandler: GetAvailableCouponsHandler,
  private val issueCouponHandler: IssueCouponHandler,
  private val changeCouponStatusHandler: ChangeCouponStatusHandler,
  private val getCouponStatusHandler: GetCouponStatusHandler,
) : IService {
  override fun gerRouter(): Router {
    val router: Router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.route("/:owner_id/availables").method(HttpMethod.GET).coroutineHandler { getAvailableCoupons(it) }
    router.route("/:owner_id/coupons/:coupon_id").method(HttpMethod.POST).coroutineHandler { issueCoupon(it) }
    router.route("/:owner_id/coupons/:coupon_id/status/:status")
      .method(HttpMethod.PUT)
      .coroutineHandler {
        internalAuthHandler.handle(it)
        changeCouponStatus(it)
      }
    router.route("/:owner_id/coupons/:coupon_id/status")
      .method(HttpMethod.GET)
      .coroutineHandler { getCouponStatus(it) }
    return router
  }

  private suspend fun getAvailableCoupons(routingContext: RoutingContext): String {
    val request = try {
      // NOTE(pjongy): Only allows last parameter for same key
      GetAvailableCouponsRequest(
        ownerId = routingContext.pathParam("owner_id").toString(),
        page = routingContext.queryParam("page").last().toInt(),
        pageSize = routingContext.queryParam("page_size").last().toInt(),
        status = routingContext.queryParam("status"),
      )
    } catch (e: Exception) {
      throw InvalidParameter(e.message ?: "Parameters not satisfied")
    }
    return getAvailableCouponsHandler.handle(request = request)
  }

  private suspend fun issueCoupon(routingContext: RoutingContext): String {
    val request = try {
      // NOTE(pjongy): Only allows last parameter for same key
      IssueCouponRequest(
        ownerId = routingContext.pathParam("owner_id").toString(),
        couponId = routingContext.pathParam("coupon_id").toString(),
      )
    } catch (e: Exception) {
      throw InvalidParameter(e.message ?: "Parameters not satisfied")
    }
    return issueCouponHandler.handle(request = request)
  }

  private suspend fun changeCouponStatus(routingContext: RoutingContext): String {
    val request = try {
      // NOTE(pjongy): Only allows last parameter for same key
      ChangeCouponStatusRequest(
        ownerId = routingContext.pathParam("owner_id").toString(),
        couponId = routingContext.pathParam("coupon_id").toString(),
        status = routingContext.pathParam("status").toString(),
      )
    } catch (e: Exception) {
      throw InvalidParameter(e.message ?: "Parameters not satisfied")
    }
    return changeCouponStatusHandler.handle(request = request)
  }

  private suspend fun getCouponStatus(routingContext: RoutingContext): String {
    val request = try {
      // NOTE(pjongy): Only allows last parameter for same key
      GetCouponStatusRequest(
        ownerId = routingContext.pathParam("owner_id").toString(),
        couponId = routingContext.pathParam("coupon_id").toString(),
      )
    } catch (e: Exception) {
      throw InvalidParameter(e.message ?: "Parameters not satisfied")
    }
    return getCouponStatusHandler.handle(request = request)
  }
}
