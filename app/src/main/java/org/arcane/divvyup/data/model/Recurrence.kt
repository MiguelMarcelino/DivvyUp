package org.arcane.divvyup.data.model

import com.google.firebase.Timestamp

data class Recurrence(
    var interval: RecurrenceInterval,
    var endDate: Timestamp? = null
) {
    constructor() : this(
        interval = RecurrenceInterval.DAILY,
        endDate = null
    )
}
