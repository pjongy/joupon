package com.github.pjongy

import com.github.pjongy.util.ConfigUtil.setConfigSource
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec

object Joupon : ConfigSpec("joupon") {
  val port by required<Int>()
  val host by required<String>()

  object MySQL : ConfigSpec("mysql") {
    val jdbcUrl by required<String>()
    val maxPoolSize by required<Int>()
    val user by required<String>()
    val password by required<String>()
  }

  val internalApiKeys by required<String>() // NOTE(pjongy): Comma separated string
}

val jouponConfig = setConfigSource(Config { addSpec(Joupon) })
