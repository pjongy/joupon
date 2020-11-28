package com.github.pjongy

import com.github.pjongy.di.ClockModule
import com.github.pjongy.di.DaggerComponent
import com.github.pjongy.di.VertxModule
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import org.slf4j.LoggerFactory


class MainVerticle : AbstractVerticle() {
  private val logger = LoggerFactory.getLogger(javaClass)

  override fun start(startPromise: Promise<Void>) {
    val joupon = DaggerComponent.builder()
      .clockModule(ClockModule())
      .vertxModule(VertxModule(vertx = vertx)).build()
    val vertx = joupon.vertx()
    logger.info("joupon service started")
    val server: HttpServer = vertx.createHttpServer()

    // TODO(pjongy): Add request handler to server
    server.listen(8080);
  }
}
