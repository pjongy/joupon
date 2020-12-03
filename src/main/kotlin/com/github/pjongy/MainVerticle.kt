package com.github.pjongy

import com.github.pjongy.di.ClockModule
import com.github.pjongy.di.DaggerComponent
import com.github.pjongy.di.VertxModule
import com.github.pjongy.model.Coupon
import com.github.pjongy.model.CouponWallet
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.Router
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {
  private val logger = LoggerFactory.getLogger(javaClass)

  override fun start(startPromise: Promise<Void>) {
    val port = jouponConfig[Joupon.port]
    val host = jouponConfig[Joupon.host]
    val joupon = DaggerComponent.builder()
      .clockModule(ClockModule())
      .vertxModule(VertxModule(vertx = vertx)).build()
    runBlocking {
      suspendedTransactionAsync(db = joupon.database()) {
        SchemaUtils.create(Coupon, CouponWallet)
      }.await()
    }
    logger.info("joupon service started $host:$port")
    val vertx = joupon.vertx()
    val couponService = joupon.couponService()
    val walletService = joupon.walletService()

    val rootRouter = Router.router(vertx)
    rootRouter.mountSubRouter("/coupons", couponService.gerRouter())
    rootRouter.mountSubRouter("/wallets", walletService.gerRouter())

    val server: HttpServer = joupon.vertx().createHttpServer()
    server
      .requestHandler(rootRouter)
      .listen(port, host)
  }
}
