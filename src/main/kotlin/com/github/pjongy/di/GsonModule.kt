package com.github.pjongy.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides

@Module
open class GsonModule {
  @Provides
  open fun provideGson(): Gson = Gson()
}
