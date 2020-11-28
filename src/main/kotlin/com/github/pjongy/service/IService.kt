package com.github.pjongy.service

import io.vertx.ext.web.Router

interface IService {
  fun gerRouter(): Router
}
