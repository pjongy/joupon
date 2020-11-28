package com.github.pjongy

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.LoggerFactory

class MainVerticle : AbstractVerticle() {
  private val logger = LoggerFactory.getLogger(javaClass)

  override fun start(startPromise: Promise<Void>) {
    logger.info("joupon service started")
    // TODO(pjongy): Run server
  }
}
