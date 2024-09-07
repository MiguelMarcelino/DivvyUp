package org.arcane.divvyup.data.model

enum class RecurrenceInterval(val expenseName: String, val days: Int) {
    DAILY("Daily", 1),
    WEEKLY("Weekly", 7),
    BIWEEKLY("Biweekly", 14),
    MONTHLY("Monthly", 30),
    YEARLY("Yearly", 365)
}