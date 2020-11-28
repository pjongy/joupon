plugins {
  id("application")
  kotlin("jvm") version "1.4.10"
  kotlin("kapt") version "1.4.20"
}

group = "com.github.pjongy"
version = "0.0.1"

repositories {
  mavenCentral()
}

val konfVersion = "0.23.0"
val vertxVersion = "3.9.4"
val log4jVersion = "2.14.0"
val slf4jVersion = "1.7.30"
val guavaVersion = "30.0-jre"

dependencies {
  implementation(kotlin("stdlib"))

  // vertx web
  implementation("io.vertx:vertx-web:$vertxVersion")
  implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
  implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")

  // Common util - guava
  implementation("com.google.guava:guava:$guavaVersion")

  // Config variable - konf
  implementation("com.uchuhimo:konf:$konfVersion")

  // Logging
  implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
  implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
  implementation("org.slf4j:slf4j-api:$slf4jVersion")
}

application {
  mainClassName = "io.vertx.core.Launcher"
}

val mainVerticleName = "com.github.pjongy.MainVerticle"

tasks {
  getByName<JavaExec>("run") {
    args = listOf(
      "run",
      mainVerticleName,
      "--redeploy=src/**/*",
      "--launcher-class=${application.mainClassName}",
      "--on-redeploy=../gradlew classes"
    )
  }
}