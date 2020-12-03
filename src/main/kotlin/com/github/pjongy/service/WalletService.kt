package com.github.pjongy.service

import com.github.pjongy.exception.InvalidParameter
import com.github.pjongy.exception.PermissionRequired
import com.github.pjongy.extension.coroutineHandler
import com.github.pjongy.handler.wallet.GetAvailableCouponsHandler
import com.github.pjongy.handler.wallet.IssueCouponHandler
import com.github.pjongy.handler.wallet.protocol.GetAvailableCouponsRequest
import com.github.pjongy.handler.wallet.protocol.IssueCouponRequest
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import javax.inject.Inject

class WalletService @Inject constructor(
  private val vertx: Vertx,
  private val getAvailableCouponsHandler: GetAvailableCouponsHandler,
  private val issueCouponHandler: IssueCouponHandler,
) : IService {
  override fun gerRouter(): Router {
    val router: Router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.route("/:owner_id/availables").method(HttpMethod.GET).coroutineHandler { getAvailableCoupons(it) }
    router.route("/:owner_id/coupons/:coupon_id").method(HttpMethod.POST).coroutineHandler { issueCoupon(it) }
    return router
  }

  private suspend fun exceptionHandler(routingContext: RoutingContext, fn: suspend () -> Unit) {
    try {
      fn()
    } catch (e: InvalidParameter) {
      routingContext.fail(400, e)
    } catch (e: PermissionRequired) {
      routingContext.fail(403, e)
    }
  }

  private suspend fun getAvailableCoupons(routingContext: RoutingContext) {
    exceptionHandler(routingContext = routingContext) {
      val response = routingContext.response()
      response.isChunked = true
      val request = try {
        // NOTE(pjongy): Only allows last parameter for same key
        GetAvailableCouponsRequest(
          ownerId = routingContext.pathParam("owner_id").first().toString(),
          page = routingContext.queryParam("page").last().toInt(),
          pageSize = routingContext.queryParam("page_size").last().toInt(),
        )
      } catch (e: Exception) {
        throw InvalidParameter(e.message ?: "Parameters not satisfied")
      }
      val responseBody = getAvailableCouponsHandler.handle(request = request)
      response.write(responseBody).end()
    }
  }

  private suspend fun issueCoupon(routingContext: RoutingContext) {
    exceptionHandler(routingContext = routingContext) {
      val response = routingContext.response()
      response.isChunked = true
      val request = try {
        // NOTE(pjongy): Only allows last parameter for same key
        IssueCouponRequest(
          ownerId = routingContext.pathParam("owner_id").toString(),
          couponId = routingContext.pathParam("coupon_id").toString(),
        )
      } catch (e: Exception) {
        throw InvalidParameter(e.message ?: "Parameters not satisfied")
      }
      val responseBody = issueCouponHandler.handle(request = request)
      response.write(responseBody).end()
    }
  }
}
