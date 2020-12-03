package com.github.pjongy.extension

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun Instant.toISO8601(zoneId: ZoneId): String =
  ZonedDateTime.ofInstant(this, zoneId).format(DateTimeFormatter.ISO_DATE_TIME)
