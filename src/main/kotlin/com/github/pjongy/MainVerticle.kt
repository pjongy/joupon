package com.github.pjongy

import com.github.pjongy.di.ClockModule
import com.github.pjongy.di.DaggerComponent
import com.github.pjongy.di.VertxModule
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {
  private val logger = LoggerFactory.getLogger(javaClass)

  override fun start(startPromise: Promise<Void>) {
    val joupon = DaggerComponent.builder()
      .clockModule(ClockModule())
      .vertxModule(VertxModule(vertx = vertx)).build()
    logger.info("joupon service started")
    val vertx = joupon.vertx()
    val couponService = joupon.couponService()

    val rootRouter = Router.router(vertx)
    rootRouter.mountSubRouter("/coupons", couponService.gerRouter())

    val server: HttpServer = joupon.vertx().createHttpServer()
    server
      .requestHandler(rootRouter)
      .listen(8080)
  }
}
