package com.github.pjongy.extension

import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit): Route {
  return handler { ctx ->
    CoroutineScope(ctx.vertx().dispatcher()).run {
      launch {
        try {
          fn(ctx)
        } catch (e: Exception) {
          println(e)
          ctx.fail(e)
        }
      }
    }
  }
}