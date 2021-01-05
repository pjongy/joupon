package com.github.pjongy.handler.coupon.extension

import com.github.pjongy.handler.coupon.protocol.Condition

val availableJoinTypes = listOf("AND", "OR")
val availableOperators = listOf(
  "int_eq",
  "int_neq",
  "int_lte",
  "int_lt",
  "int_gte",
  "int_gt",
  "str_eq",
  "str_neq",
  "contains",
)

fun Condition.isValid(): Boolean {
  if (availableJoinTypes.contains(joinType) && conditions != null) {
    if (key == null && value == null && operator == null) {
      val results = conditions.map { it.isValid() }
      return !results.contains(false) && results.isNotEmpty()
    }
  }

  if (availableOperators.contains(operator) && key != null && value != null) {
    if (joinType == null && conditions == null) {
      return true
    }
  }

  if (key == null && value == null && operator == null) {
    if (joinType == null && conditions == null) {
      return true
    }
  }

  return false
}
