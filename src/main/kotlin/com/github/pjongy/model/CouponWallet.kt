package com.github.pjongy.model

import com.github.pjongy.extension.varcharUTF8
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.`java-time`.timestamp
import java.util.UUID

object CouponWallet : UUIDTable("coupon_wallet") {
  val coupon = reference("coupon", Coupon)
  val ownerId: Column<String> = varcharUTF8("owner_id")
  val createdAt = timestamp("created_at")
}

class CouponWalletEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
  companion object : UUIDEntityClass<CouponWalletEntity>(CouponWallet)
  var coupon by CouponEntity referencedOn CouponWallet.coupon
  val ownerId by CouponWallet.ownerId
  var createdAt by Coupon.createdAt
}
