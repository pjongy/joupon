package com.github.pjongy.di

import com.github.pjongy.Joupon
import com.github.pjongy.jouponConfig
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
open class InternalAuthModule {
  @Provides
  @Singleton
  @Named("INTERNAL_API_KEYS")
  open fun provideInternalApiKeys(): List<String> = jouponConfig[Joupon.internalApiKeys].split(",")
}
