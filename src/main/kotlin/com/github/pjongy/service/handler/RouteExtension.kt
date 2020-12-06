package com.github.pjongy.service.handler

import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.slf4j.MDCContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.UUID

val logger: Logger = LoggerFactory.getLogger("route")

fun Route.coroutineHandler(fn: suspend (RoutingContext) -> String): Route {
  return handler { ctx ->
    CoroutineScope(ctx.vertx().dispatcher()).run {
      MDC.put("request_id", UUID.randomUUID().toString())
      launch(MDCContext()) {
        try {
          AccessLogHandler.handle(routingContext = ctx)
          ResponseHandler.handle(routingContext = ctx) {
            fn(ctx)
          }
        } catch (e: Exception) {
          logger.error(e.message)
          ctx.fail(e)
        }
      }
    }
  }
}
