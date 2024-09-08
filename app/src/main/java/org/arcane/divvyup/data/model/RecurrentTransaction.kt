package org.arcane.divvyup.data.model

import com.google.firebase.Timestamp
import java.util.UUID

data class RecurrentTransaction(
    var uid: String,
    var interval: RecurrenceInterval,
    var startDate: Timestamp,
    var endDate: Timestamp?
) {
    constructor() : this(
        uid = UUID.randomUUID().toString(),
        interval = RecurrenceInterval.DAILY,
        startDate = Timestamp.now(),
        endDate = null
    )

    constructor(
        interval: RecurrenceInterval,
        startDate: Timestamp,
        endDate: Timestamp?
    ) : this(
        uid = UUID.randomUUID().toString(),
        interval = interval,
        startDate = startDate,
        endDate = endDate
    )

    constructor(
        interval: RecurrenceInterval,
        endDate: Timestamp?
    ) : this(
        uid = UUID.randomUUID().toString(),
        interval = interval,
        startDate = Timestamp.now(),
        endDate = endDate
    )
}

enum class RecurrenceInterval(val expenseName: String, val days: Int) {
    DAILY("Daily", 1),
    WEEKLY("Weekly", 7),
    BIWEEKLY("Biweekly", 14),
    MONTHLY("Monthly", 30),
    YEARLY("Yearly", 365)
}
