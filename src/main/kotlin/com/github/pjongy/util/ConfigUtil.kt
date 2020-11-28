package com.github.pjongy.util

import com.google.common.base.CaseFormat
import com.google.common.base.Strings
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.base.FlatSource
import com.uchuhimo.konf.source.yaml

object ConfigUtil {
  fun getCurrentEnv(name: String = "APP_CONFIG"): String? = Strings.emptyToNull(
    System.getenv(name)
  )

  fun setConfigSource(block: Config, configFileDir: String = "config"): Config {
    val yamlExtension = "yaml"
    return block
      .from.yaml.resource("$configFileDir/default.$yamlExtension")
      .let {
        getCurrentEnv()?.let { env ->
          it.from.yaml.resource("$configFileDir/$env.$yamlExtension")
        } ?: it
      }.withSource(
        FlatSource(
          // Convert environment variable like PARENT__CHILD_ELEMENT as parent.childElement
          System.getenv().mapKeys { (key, _) ->
            val seperated = key.toLowerCase().replace("__", ".")
            CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, seperated)
          },
          type = "system-environment"
        )
      )
  }
}
