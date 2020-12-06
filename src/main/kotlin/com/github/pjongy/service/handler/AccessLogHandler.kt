package com.github.pjongy.service.handler

import com.github.pjongy.extension.toISO8601
import io.vertx.ext.web.RoutingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.ZoneOffset

object AccessLogHandler {
  private val logger: Logger = LoggerFactory.getLogger("access.log")

  fun handle(routingContext: RoutingContext) {
    val request = routingContext.request()
    val method = request.method()
    val remoteHost = request.remoteAddress().host()
    val version = request.version().name
    val requestPath = request.path()
    val queryParams = request.query()
    val currentTime = Instant.now().toISO8601(ZoneOffset.UTC)
    logger.info("$currentTime $method $version $remoteHost $requestPath?$queryParams")
  }
}
