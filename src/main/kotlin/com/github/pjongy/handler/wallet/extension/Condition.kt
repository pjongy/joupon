package com.github.pjongy.handler.wallet.extension

import com.github.pjongy.handler.wallet.protocol.Condition
import com.github.pjongy.handler.wallet.protocol.ConditionProperty

fun Condition.isAvailable(properties: List<ConditionProperty>): Boolean {
  conditions?.let { conditions ->
    return when (joinType) {
      "AND" -> !conditions.map { it.isAvailable(properties) }.contains(false)
      "OR" -> conditions.map { it.isAvailable(properties) }.contains(true)
      else -> throw Exception("wrong join type: $joinType")
    }
  }

  if (key != null && value != null && operator != null) {
    val results = properties.filter { property ->
      property.key == key
    }.mapNotNull { property ->
      when (operator) {
        "int_eq" -> property.value.toInt() == value.toInt()
        "int_neq" -> property.value.toInt() != value.toInt()
        "int_lte" -> property.value.toInt() <= value.toInt()
        "int_lt" -> property.value.toInt() < value.toInt()
        "int_gte" -> property.value.toInt() >= value.toInt()
        "int_gt" -> property.value.toInt() > value.toInt()
        "str_eq" -> property.value == value
        "str_neq" -> property.value != value
        "contains" -> value.contains(property.value)
        else -> null
      }
    }
    return !results.contains(false) && results.isNotEmpty()
  }
  return false
}
