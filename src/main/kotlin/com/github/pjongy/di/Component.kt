package com.github.pjongy.di

import dagger.Component
import io.vertx.core.Vertx
import java.time.Clock
import javax.inject.Singleton


@Singleton
@Component(
  modules = [
    ClockModule::class,
    VertxModule::class
  ]
)
interface Component {
  fun clock(): Clock
  fun vertx(): Vertx
}