package com.github.pjongy.service

import com.github.pjongy.exception.InvalidParameter
import com.github.pjongy.extension.coroutineHandler
import com.github.pjongy.handler.coupon.CreateCouponHandler
import com.github.pjongy.handler.coupon.GetCouponsHandler
import com.github.pjongy.handler.coupon.protocol.CreateCouponRequest
import com.github.pjongy.handler.coupon.protocol.GetCouponsRequest
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import javax.inject.Inject

class CouponService @Inject constructor(
  private val vertx: Vertx,
  private val getCouponsHandler: GetCouponsHandler,
  private val createCouponHandler: CreateCouponHandler,
) : IService {
  override fun gerRouter(): Router {
    val router: Router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.route("/").method(HttpMethod.POST).coroutineHandler { createCoupon(it) }
    router.route("/").method(HttpMethod.GET).coroutineHandler { getCoupons(it) }
    return router
  }

  private suspend fun createCoupon(routingContext: RoutingContext): String {
    val request = try {
      val bodyJson = routingContext.bodyAsJson
      CreateCouponRequest(
        name = bodyJson.getString("name"),
        category = bodyJson.getString("category"),
        totalAmount = bodyJson.getInteger("total_amount"),
        discountRate = bodyJson.getFloat("discount_rate"),
        discountAmount = bodyJson.getInteger("discount_amount"),
        expiredAt = bodyJson.getString("expired_at"),
      )
    } catch (e: Exception) {
      throw InvalidParameter(e.message ?: "Parameters not satisfied")
    }
    return createCouponHandler.handle(request = request)
  }

  private suspend fun getCoupons(routingContext: RoutingContext): String {
    val request = try {
      // NOTE(pjongy): Only allows last parameter for same key
      GetCouponsRequest(
        page = routingContext.queryParam("page").last().toInt(),
        pageSize = routingContext.queryParam("page_size").last().toInt(),
      )
    } catch (e: Exception) {
      throw InvalidParameter(e.message ?: "Parameters not satisfied")
    }
    return getCouponsHandler.handle(request = request)
  }
}
