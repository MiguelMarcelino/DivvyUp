package org.arcane.divvyup.data

import com.google.firebase.Timestamp
import org.arcane.divvyup.data.model.TransactionType
import org.arcane.divvyup.data.model.Recurrence
import java.util.UUID

data class Transaction(
    var uid: String,
    var title: String,
    var amount: Double,
    var type: TransactionType,
    var recurrence: Recurrence?,
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
        recurrence = null,
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
        recurrence: Recurrence?,
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
        recurrence = recurrence,
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