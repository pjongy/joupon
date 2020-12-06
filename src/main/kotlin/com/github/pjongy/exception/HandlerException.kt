package com.github.pjongy.exception

open class HandlerException(message: String) : RuntimeException(message)

open class InvalidParameter(message: String) : HandlerException(message)
open class PermissionRequired(message: String) : HandlerException(message)
open class AuthorizationRequired(message: String) : HandlerException(message)
open class Duplicated(message: String) : HandlerException(message)
open class UnAvailableData(message: String) : HandlerException(message)
open class NotFound(message: String) : HandlerException(message)
