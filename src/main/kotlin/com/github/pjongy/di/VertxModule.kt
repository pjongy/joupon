package com.github.pjongy.di

import dagger.Module
import dagger.Provides
import io.vertx.core.Vertx
import javax.inject.Singleton


@Module
open class VertxModule constructor(
  private val vertx: Vertx
) {
  @Provides
  @Singleton
  open fun provideVertx(): Vertx = vertx
}