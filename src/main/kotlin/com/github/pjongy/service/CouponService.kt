package com.github.pjongy.service

import com.github.pjongy.exception.InvalidParameter
import com.github.pjongy.handler.coupon.CreateCouponHandler
import com.github.pjongy.handler.coupon.GetCouponsHandler
import com.github.pjongy.handler.coupon.GetCouponsWithUsageStatusHandler
import com.github.pjongy.handler.coupon.protocol.CreateCouponRequest
import com.github.pjongy.handler.coupon.protocol.CreateCouponRequestBody
import com.github.pjongy.handler.coupon.protocol.GetCouponsRequest
import com.github.pjongy.handler.coupon.protocol.GetCouponsWithUsageStatusRequest
import com.github.pjongy.service.handler.InternalAuthHandler
import com.github.pjongy.service.handler.coroutineHandler
import com.google.gson.Gson
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import javax.inject.Inject

class CouponService @Inject constructor(
  private val vertx: Vertx,
  private val gson: Gson,
  private val internalAuthHandler: InternalAuthHandler,
  private val getCouponsHandler: GetCouponsHandler,
  private val createCouponHandler: CreateCouponHandler,
  private val getCouponsWithUsageStatusHandler: GetCouponsWithUsageStatusHandler,
) : IService {
  override fun gerRouter(): Router {
    val router: Router = Router.router(vertx)
    router.route().handler(BodyHandler.create())
    router.route("/").method(HttpMethod.POST).coroutineHandler {
      internalAuthHandler.handle(it)
      createCoupon(it)
    }
    router.route("/").method(HttpMethod.GET).coroutineHandler {
      internalAuthHandler.handle(it)
      getCoupons(it)
    }
    router.route("/fetch-with-issued").method(HttpMethod.GET).coroutineHandler {
      internalAuthHandler.handle(it)
      getCouponWithIssuedTotal(it)
    }
    return router
  }

  private suspend fun createCoupon(routingContext: RoutingContext): String {
    val request = try {
      val requestBody = gson.fromJson(
        routingContext.bodyAsString,
        CreateCouponRequestBody::class.java,
      )
      CreateCouponRequest(
        name = requestBody.name,
        category = requestBody.category,
        description = requestBody.description,
        discountAmount = requestBody.discountAmount,
        discountRate = requestBody.discountRate,
        expiredAt = requestBody.expiredAt,
        imageUrl = requestBody.imageUrl,
        totalAmount = requestBody.totalAmount,
        issuingCondition = requestBody.issuingCondition,
        usingCondition = requestBody.usingCondition,
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

  private suspend fun getCouponWithIssuedTotal(routingContext: RoutingContext): String {
    val request = try {
      // NOTE(pjongy): Only allows last parameter for same key
      GetCouponsWithUsageStatusRequest(
        couponIds = routingContext.queryParam("coupon_ids"),
      )
    } catch (e: Exception) {
      throw InvalidParameter(e.message ?: "Parameters not satisfied")
    }
    return getCouponsWithUsageStatusHandler.handle(request = request)
  }
}
