package com.github.pjongy.extension

import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.Transaction

class Union(private val queries: List<Query>) : Query(queries[0].set, null) {
  // NOTE(pjongy): Surprisingly exposed does not have UNION (https://github.com/JetBrains/Exposed/issues/402)
  override fun arguments(): List<ArrayList<Pair<IColumnType, Any?>>> {
    val allArgs = queries.map { it.arguments() }
    var answer: List<ArrayList<Pair<IColumnType, Any?>>> = listOf()
    allArgs.forEach {
      answer = answer.zipLongest(it) { a, b ->
        a + b
      }
    }
    return answer
  }

  override fun prepareSQL(transaction: Transaction): String =
    queries.joinToString(" UNION ") { it.prepareSQL(transaction) }
}

private operator fun <T> Collection<T>?.plus(other: Collection<T>?): ArrayList<T> {
  val result = ArrayList<T>(this?.size + other?.size)
  if (this != null) {
    result.addAll(this)
  }
  if (other != null) {
    result.addAll(other)
  }
  return result
}

private operator fun Int?.plus(other: Int?) = when {
  this == null && other == null -> 10
  this == null -> other!!
  other == null -> this
  else -> this + other
}

private inline fun <T, R, V> Iterable<T>.zipLongest(other: Iterable<R>, transform: (a: T?, b: R?) -> V): List<V> {
  val first = iterator()
  val second = other.iterator()
  val list = ArrayList<V>(minOf(collectionSizeOrDefault(10), other.collectionSizeOrDefault(10)))
  while (first.hasNext() || second.hasNext()) {
    if (first.hasNext() && second.hasNext()) {
      list.add(transform(first.next(), second.next()))
    } else if (first.hasNext()) {
      list.add(transform(first.next(), null))
    } else {
      list.add(transform(null, second.next()))
    }
  }
  return list
}

private fun <T> Iterable<T>.collectionSizeOrDefault(default: Int): Int =
  if (this is Collection<*>) this.size else default
