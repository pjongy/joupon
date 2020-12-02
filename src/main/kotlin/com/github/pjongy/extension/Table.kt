package com.github.pjongy.extension

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.VarCharColumnType


fun Table.varcharUTF8(
  name: String,
  length: Int = 255,
  collate: String = "utf8mb4_unicode_ci"
): Column<String> = registerColumn(name, VarCharColumnType(length, collate))
