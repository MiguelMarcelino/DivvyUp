package org.arcane.divvyup.data

import com.google.firebase.Timestamp
import org.arcane.divvyup.data.identifiers.UserIdentifier
import org.arcane.divvyup.data.model.ExpenseType
import org.arcane.divvyup.data.model.Recurrence
import java.util.UUID

data class Expense(
    var uid: String,
    var title: String,
    var amount: Double,
    var type: ExpenseType,
    var recurrence: Recurrence?,
    var description: String?,
    var currency: String,
    var status: String,
    var tags: List<String>,
    var expenseShare: Map<String, Double>?,
    var ownerUid: String,
    var groupUid: String,
    var userUids: List<String>,
    var createdAt: Timestamp
) {
    constructor() : this(
        uid = UUID.randomUUID().toString(),
        title = "",
        amount = 0.0,
        type = ExpenseType.OTHER,
        recurrence = null,
        description = "",
        currency = "",
        status = "",
        tags = listOf(),
        expenseShare = mapOf(),
        ownerUid = "",
        groupUid = "",
        userUids = listOf(),
        createdAt = Timestamp.now()
    )

    constructor(
        title: String,
        amount: Double,
        type: ExpenseType,
        recurrence: Recurrence?,
        description: String?,
        currency: String,
        status: String,
        tags: List<String>,
        expenseShare: Map<String, Double>?,
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
        expenseShare = expenseShare,
        ownerUid = ownerUid,
        groupUid = groupUid,
        userUids = userUids,
        createdAt = Timestamp.now()
    )
}