package com.github.pjongy.di

import dagger.Module
import dagger.Provides
import java.time.Clock
import javax.inject.Singleton


@Module
open class ClockModule {
  @Provides
  @Singleton
  open fun provideClock(): Clock = Clock.systemUTC()
}