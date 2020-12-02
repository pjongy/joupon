package com.github.pjongy.model

import com.github.pjongy.extension.varcharUTF8
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.util.UUID

object Coupon : UUIDTable("coupon") {
  val totalAmount: Column<Int> = integer("total_amount")
  val discountRate: Column<Float?> = float("discount_rate").nullable()
  val discountAmount: Column<Int?> = integer("discount_amount").nullable()
  val category: Column<String> = varcharUTF8("category", 32).index()
  val name: Column<String> = varcharUTF8("name", 32)
  val createdAt = timestamp("created_at")
}

class CouponEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
  companion object : UUIDEntityClass<CouponEntity>(Coupon)
  var totalAmount by Coupon.totalAmount
  var discountRate by Coupon.discountRate
  var discountAmount by Coupon.discountAmount
  var category by Coupon.category
  var name by Coupon.name
  var createdAt by Coupon.createdAt
}
