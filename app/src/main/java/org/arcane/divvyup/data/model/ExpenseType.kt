package org.arcane.divvyup.data.model

enum class ExpenseType(expenseName: String) {
    INCOME("Income") {
        override fun value(): Int = 1
    },
    GYM("Gym"),
    FOOD("Food"),
    TRANSPORT("Transport"),
    ENTERTAINMENT("Entertainment"),
    SHOPPING("Shopping"),
    UTILITIES("Utilities"),
    OTHER("Other");

    /**
     * Designates the value of each Expense type. By default, it is negative,
     * meaning it will deduct from the total balance.
     * We chose to use an integer instead of a boolean, as we could potentially
     * use it to represent a percentage multiplier for each expense.
     */
    open fun value(): Int = -1
}

