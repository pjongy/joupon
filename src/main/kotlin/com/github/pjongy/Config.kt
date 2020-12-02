package com.github.pjongy

import com.github.pjongy.util.ConfigUtil.setConfigSource
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.Config

object Joupon : ConfigSpec("joupon") {
  val port by required<Int>()
  val host by required<String>()
}

val jouponConfig = setConfigSource(Config { addSpec(Joupon) })
