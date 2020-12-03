package com.github.pjongy.repository

import org.jetbrains.exposed.sql.Database
import java.time.Clock
import javax.inject.Inject

class CouponRepository @Inject constructor(
  private val db: Database,
  private val clock: Clock,
)
