package com.github.pjongy.model

import com.github.pjongy.extension.varcharUTF8
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.time.Instant
import java.util.UUID

enum class CouponWalletStatus {
  UNUSED,
  USING,
  USED,
}

object CouponWallet : UUIDTable("coupon_wallet") {
  val couponId = uuid("coupon_id")
  val ownerId: Column<String> = varcharUTF8("owner_id")
  val createdAt = timestamp("created_at")
  val status = enumeration("status", CouponWalletStatus::class)
    .default(CouponWalletStatus.UNUSED)
    .index()
}

data class CouponWalletRow(
  val id: UUID,
  val couponId: UUID,
  val ownerId: String,
  val createdAt: Instant,
  val status: CouponWalletStatus,
)

fun wrapCouponWalletRow(row: ResultRow): CouponWalletRow {
  return CouponWalletRow(
    id = row[CouponWallet.id].value,
    couponId = row[CouponWallet.couponId],
    ownerId = row[CouponWallet.ownerId],
    createdAt = row[CouponWallet.createdAt],
    status = row[CouponWallet.status],
  )
}
