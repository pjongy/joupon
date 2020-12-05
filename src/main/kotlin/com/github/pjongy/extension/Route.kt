package com.github.pjongy.extension

import com.github.pjongy.exception.Duplicated
import com.github.pjongy.exception.InvalidParameter
import com.github.pjongy.exception.PermissionRequired
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

suspend fun exceptionHandler(routingContext: RoutingContext, fn: suspend () -> String) {
  val response = routingContext.response()
  val responseBody = try {
    response.statusCode = 200
    fn()
  } catch (e: InvalidParameter) {
    response.statusCode = 400
    e.message
  } catch (e: PermissionRequired) {
    response.statusCode = 403
    e.message
  } catch (e: Duplicated) {
    response.statusCode = 409
    e.message
  }

  response.write(responseBody)
  response.end()
}

fun Route.coroutineHandler(fn: suspend (RoutingContext) -> String): Route {
  return handler { ctx ->
    CoroutineScope(ctx.vertx().dispatcher()).run {
      launch {
        try {
          val response = ctx.response()
          response.isChunked = true
          response.putHeader("Content-Type", "application/json")
          exceptionHandler(routingContext = ctx) {
            fn(ctx)
          }
        } catch (e: Exception) {
          ctx.fail(e)
        }
      }
    }
  }
}
