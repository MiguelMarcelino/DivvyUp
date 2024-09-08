package org.arcane.divvyup.data.model

import com.google.firebase.Timestamp
import java.util.UUID

data class Transaction(
    var uid: String,
    var title: String,
    var amount: Double,
    var type: TransactionType,
    var description: String?,
    var currency: String,
    var status: String,
    var tags: List<String>,
    var share: Map<String, Double>?,
    var ownerUid: String,
    var groupUid: String,
    var userUids: List<String>,
    var createdAt: Timestamp
) {
    constructor() : this(
        uid = UUID.randomUUID().toString(),
        title = "",
        amount = 0.0,
        type = TransactionType.OTHER,
        description = "",
        currency = "",
        status = "",
        tags = listOf(),
        share = mapOf(),
        ownerUid = "",
        groupUid = "",
        userUids = listOf(),
        createdAt = Timestamp.now()
    )

    constructor(
        title: String,
        amount: Double,
        type: TransactionType,
        description: String?,
        currency: String,
        status: String,
        tags: List<String>,
        share: Map<String, Double>?,
        ownerUid: String,
        groupUid: String,
        userUids: List<String>
    ) : this(
        title = title,
        uid = UUID.randomUUID().toString(),
        amount = amount,
        type = type,
        description = description,
        currency = currency,
        status = status,
        tags = tags,
        share = share,
        ownerUid = ownerUid,
        groupUid = groupUid,
        userUids = userUids,
        createdAt = Timestamp.now()
    )
}