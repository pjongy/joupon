package com.github.pjongy.service.handler

import com.github.pjongy.exception.AuthorizationRequired
import com.github.pjongy.exception.PermissionRequired
import io.vertx.ext.web.RoutingContext
import javax.inject.Inject
import javax.inject.Named

class InternalAuthHandler @Inject constructor(
  @Named("INTERNAL_API_KEYS")
  private val internalApiKeys: List<String>
) {
  companion object {
    const val INTERNAL_AUTH_CUSTOM_HEADER = "X-Internal-Key"
  }

  private fun isPermitted(key: String): Boolean {
    return internalApiKeys.contains(key)
  }

  fun handle(routingContext: RoutingContext) {
    val internalAuthHeader = routingContext.request().getHeader(INTERNAL_AUTH_CUSTOM_HEADER)
      ?: throw AuthorizationRequired("$INTERNAL_AUTH_CUSTOM_HEADER is not included")
    if (!isPermitted(internalAuthHeader)) {
      throw PermissionRequired("internal api key is not permitted")
    }
  }
}
