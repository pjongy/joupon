package com.github.pjongy.model

import com.github.pjongy.extension.textUTF8
import com.github.pjongy.extension.varcharUTF8
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant
import java.util.UUID

enum class CouponStatus {
  NORMAL,
  DELETED,
}

object Coupon : UUIDTable("coupon") {
  val totalAmount: Column<Int> = integer("total_amount")
  val discountRate: Column<Float?> = float("discount_rate").nullable()
  val discountAmount: Column<Int?> = integer("discount_amount").nullable()
  val category: Column<String> = varcharUTF8("category", 32).index()
  val name: Column<String> = varcharUTF8("name", 32)
  val description: Column<String> = textUTF8("description")
  val imageUrl: Column<String> = textUTF8("image_url")
  val createdAt = timestamp("created_at")
  val expiredAt = timestamp("expired_at")
  val status = enumeration("status", CouponStatus::class)
    .default(CouponStatus.NORMAL)
    .index()
  val issuingCondition: Column<String> = textUTF8("issuing_condition")
  val usingCondition: Column<String> = textUTF8("using_condition")
}

data class CouponRow(
  val id: UUID,
  val totalAmount: Int,
  val discountRate: Float?,
  val discountAmount: Int?,
  val category: String,
  val name: String,
  val description: String,
  val imageUrl: String,
  val createdAt: Instant,
  val expiredAt: Instant,
  val status: CouponStatus,
  val issuingCondition: String, // NOTE(pjongy): Json string that represent condition for issue coupon
  val usingCondition: String, // NOTE(pjongy): Json string that represent condition for use coupon
)

fun wrapCouponRow(row: ResultRow): CouponRow {
  return CouponRow(
    id = row[Coupon.id].value,
    totalAmount = row[Coupon.totalAmount],
    discountRate = row[Coupon.discountRate],
    discountAmount = row[Coupon.discountAmount],
    category = row[Coupon.category],
    name = row[Coupon.name],
    description = row[Coupon.description],
    imageUrl = row[Coupon.imageUrl],
    createdAt = row[Coupon.createdAt],
    expiredAt = row[Coupon.expiredAt],
    status = row[Coupon.status],
    issuingCondition = row[Coupon.issuingCondition],
    usingCondition = row[Coupon.usingCondition],
  )
}

data class CouponWithUsageStatus(
  val usedTotal: Long,
  val unusedTotal: Long,
  val usingTotal: Long,
  val coupon: CouponRow,
)
