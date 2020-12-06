package com.github.pjongy.extension

import com.github.pjongy.exception.AuthorizationRequired
import com.github.pjongy.exception.Duplicated
import com.github.pjongy.exception.InvalidParameter
import com.github.pjongy.exception.NotFound
import com.github.pjongy.exception.UnAvailableData
import com.github.pjongy.exception.PermissionRequired
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val logger: Logger = LoggerFactory.getLogger("router")

suspend fun responseHandler(routingContext: RoutingContext, fn: suspend () -> String) {
  val response = routingContext.response()
  response.isChunked = true
  response.putHeader("Content-Type", "application/json")
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
  } catch (e: AuthorizationRequired) {
    response.statusCode = 401
    e.message
  } catch (e: NotFound) {
    response.statusCode = 404
    e.message
  } catch (e: UnAvailableData) {
    response.statusCode = 404
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
          responseHandler(routingContext = ctx) { fn(ctx) }
        } catch (e: Exception) {
          logger.error(e)
          ctx.fail(e)
        }
      }
    }
  }
}
