package com.github.pjongy.service

import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import javax.inject.Inject

class CouponService @Inject constructor(
  private val vertx: Vertx
) : IService {
  override fun gerRouter(): Router {
    val router: Router = Router.router(vertx)
    // TODO(pjongy): Wire handler to router with resource paths
    return router
  }
}