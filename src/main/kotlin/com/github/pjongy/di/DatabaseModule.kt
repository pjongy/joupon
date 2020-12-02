package com.github.pjongy.di

import com.github.pjongy.Joupon
import com.github.pjongy.jouponConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dagger.Module
import dagger.Provides
import org.jetbrains.exposed.sql.Database
import javax.inject.Singleton

@Module
class DatabaseModule {
  @Provides
  @Singleton
  fun provideHikariConfig(): HikariConfig {
    val config = HikariConfig()
    config.driverClassName = "com.mysql.cj.jdbc.Driver"
    config.isAutoCommit = true
    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    config.jdbcUrl = jouponConfig[Joupon.MySQL.jdbcUrl]
    config.username = jouponConfig[Joupon.MySQL.user]
    config.password = jouponConfig[Joupon.MySQL.password]
    config.maximumPoolSize = jouponConfig[Joupon.MySQL.maxPoolSize]
    config.validate()
    return config
  }

  @Provides
  @Singleton
  fun provideDatabase(hikariConfig: HikariConfig): Database {
    return Database.connect(HikariDataSource(hikariConfig))
  }
}
