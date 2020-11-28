package com.github.pjongy.exception

open class HandlerException(message: String) : RuntimeException(message)

open class InvalidParameter(message: String) : HandlerException(message)
open class PermissionRequired(message: String) : HandlerException(message)