package org.arcane.divvyup.data.model

enum class TransactionType(val transactionName: String) {
    GYM("Gym"),
    FOOD("Food"),
    TRANSPORTATION("Transportation"),
    ENTERTAINMENT("Entertainment"),
    SHOPPING("Shopping"),
    UTILITIES("Utilities"),
    SUBSCRIPTION("Subscription"),
    OTHER("Other"),

    // TODO: Split into separate IncomeType
    SALARY("Salary") {
        override fun value(): Int = 1
     },
    BONUS("Bonus") {
        override fun value(): Int = 1
    },
    GIFT("Gift") {
        override fun value(): Int = 1
    },
    REFUND("Refund") {
        override fun value(): Int = 1
    };

    /**
     * Designates the value of each Expense type. By default, it is negative,
     * meaning it will deduct from the total balance.
     * We chose to use an integer instead of a boolean, as we could potentially
     * use it to represent a percentage multiplier for each expense.
     */
    open fun value(): Int = -1
}