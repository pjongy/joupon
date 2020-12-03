package com.github.pjongy.di

import com.github.pjongy.service.CouponService
import com.github.pjongy.service.WalletService
import dagger.Component
import io.vertx.core.Vertx
import org.jetbrains.exposed.sql.Database
import java.time.Clock
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    ClockModule::class,
    VertxModule::class,
    GsonModule::class,
    DatabaseModule::class,
  ]
)
interface Component {
  fun clock(): Clock
  fun database(): Database
  fun vertx(): Vertx
  fun couponService(): CouponService
  fun walletService(): WalletService
}
