package com.github.pjongy.service.handler

import com.github.pjongy.exception.AuthorizationRequired
import com.github.pjongy.exception.Duplicated
import com.github.pjongy.exception.InvalidParameter
import com.github.pjongy.exception.NotFound
import com.github.pjongy.exception.PermissionRequired
import com.github.pjongy.exception.UnAvailableData
import io.vertx.ext.web.RoutingContext

object ResponseHandler {
  private fun errorAsJson(errorMessage: String?): String {
    return "{\"error\": \"${errorMessage?.replace("\"", "\\\"") ?: ""}\"}"
  }

  suspend fun handle(routingContext: RoutingContext, fn: suspend () -> String) {
    val response = routingContext.response()
    response.isChunked = true
    response.putHeader("Content-Type", "application/json")
    val responseBody = try {
      response.statusCode = 200
      fn()
    } catch (e: InvalidParameter) {
      response.statusCode = 400
      errorAsJson(e.message)
    } catch (e: PermissionRequired) {
      response.statusCode = 403
      errorAsJson(e.message)
    } catch (e: Duplicated) {
      response.statusCode = 409
      errorAsJson(e.message)
    } catch (e: AuthorizationRequired) {
      response.statusCode = 401
      errorAsJson(e.message)
    } catch (e: NotFound) {
      response.statusCode = 404
      errorAsJson(e.message)
    } catch (e: UnAvailableData) {
      response.statusCode = 404
      errorAsJson(e.message)
    }
    response.write(responseBody)
    response.end()
  }
}
