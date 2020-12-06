plugins {
  id("application")
  kotlin("jvm") version "1.4.10"
  kotlin("kapt") version "1.4.20"
}

group = "com.github.pjongy"
version = "0.0.1"

repositories {
  mavenCentral()
  jcenter()
}

val daggerVersion = "2.30.1"
val konfVersion = "0.23.0"
val vertxVersion = "3.9.4"
val log4jVersion = "2.14.0"
val slf4jVersion = "1.7.30"
val guavaVersion = "30.0-jre"
val ktlintVersion = "0.39.0"
val gsonVersion = "2.8.6"
val exposedVersion = "0.28.1"
val hikariVersion = "3.4.5"
val mysqlConnectorVersion = "8.0.22"
val kotlinxVersion = "1.4.2"
val ktlint by configurations.creating

dependencies {
  implementation(kotlin("stdlib"))

  // Kotlin coroutine
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinxVersion")

  // vertx web
  implementation("io.vertx:vertx-web:$vertxVersion")
  implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
  implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")

  // Common util - guava
  implementation("com.google.guava:guava:$guavaVersion")

  // DI - Dagger
  implementation("com.google.dagger:dagger:$daggerVersion")
  kapt("com.google.dagger:dagger-compiler:$daggerVersion")

  // Json - gson
  implementation("com.google.code.gson:gson:$gsonVersion")

  // Config variable - konf
  implementation("com.uchuhimo:konf:$konfVersion")

  // ORM - exposed
  implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
  implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
  implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
  implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

  // DBCP - hikariCP
  implementation("com.zaxxer:HikariCP:$hikariVersion")

  // MySQLJDBC
  implementation("mysql:mysql-connector-java:$mysqlConnectorVersion")

  // Logging
  implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
  implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
  implementation("org.slf4j:slf4j-api:$slf4jVersion")

  // Link - ktlint
  ktlint("com.pinterest:ktlint:$ktlintVersion")
}

application {
  mainClassName = "io.vertx.core.Launcher"
}

val mainVerticleName = "com.github.pjongy.MainVerticle"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
  kotlinOptions {
    jvmTarget = "12"
  }
}

tasks {
  getByName<JavaExec>("run") {
    dependsOn("compileKotlin")
    args = listOf(
      "run",
      mainVerticleName,
      "--launcher-class=${application.mainClassName}"
    )
  }

  register<JavaExec>("ktlintCheck") {
    description = "Check Kotlin code style."
    main = "com.pinterest.ktlint.Main"
    classpath = ktlint
    args = listOf("src/**/*.kt")
  }

  register<JavaExec>("ktlintFormat") {
    description = "Fix Kotlin code style deviations."
    main = "com.pinterest.ktlint.Main"
    classpath = ktlint
    args = listOf("-F", "src/**/*.kt")
  }
}
