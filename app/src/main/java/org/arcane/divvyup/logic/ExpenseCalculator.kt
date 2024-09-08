package org.arcane.divvyup.logic

import org.arcane.divvyup.data.model.Balance
import org.arcane.divvyup.data.model.Debt
import org.arcane.divvyup.data.model.Transaction
import org.arcane.divvyup.data.model.User
import org.arcane.divvyup.data.identifiers.GroupIdentifier

class ExpenseCalculator {

    fun calculateBalances(
        users: List<User>,
        expens: List<Transaction>,
        groupId: GroupIdentifier
    ): List<Balance> {
        // A map to store each user's balance
        val userBalances = mutableMapOf<String, Double>()

        // Initialize balances for each user
        users.forEach { user ->
            userBalances[user.uid] = 0.0
        }

        // Calculate total expense and each user's contribution
        expens.filter { it.groupUid == groupId.uid }.forEach { expense ->
            val ownerUid = expense.ownerUid
            // The owner is owed the value of the expense
            userBalances[ownerUid] =
                userBalances[ownerUid]?.plus(expense.amount) ?: expense.amount
        }

        val totalExpense = userBalances.values.sum()
        val numUsers = users.size
        val sharePerUser = totalExpense / numUsers

        // Calculate what each user owes or is owed
        userBalances.forEach { (userId, paidAmount) ->
            userBalances[userId] = paidAmount - sharePerUser
        }

        return userBalances.map { Balance(it.key, it.value) }
    }

    fun calculateDebt(transaction: Transaction): List<Debt> {
        val userBalances = mutableMapOf<String, Double>()

        val userIdentifiers = transaction.userUids

        // Initialize balances for each user
        userIdentifiers.forEach { userUid ->
            userBalances[userUid] = 0.0
        }

        // Calculate each user's net balance
        if (transaction.share != null) {
            transaction.share!!.forEach { (userId, sharePercentage) ->
                val shareAmount = transaction.amount * sharePercentage
                userBalances[userId] = userBalances[userId]?.minus(shareAmount) ?: -shareAmount
                userBalances[transaction.ownerUid] =
                    userBalances[transaction.ownerUid]?.plus(shareAmount) ?: shareAmount
            }
        } else {
            // If expenseShare is null, the owner pays the total amount
            userBalances[transaction.ownerUid] =
                userBalances[transaction.ownerUid]?.plus(transaction.amount) ?: transaction.amount
        }

        // Calculate total expense and fair share per user
        val totalExpense = userBalances.values.sum()
        val sharePerUser = totalExpense / userIdentifiers.size

        // Calculate net balances for each user
        userIdentifiers.forEach { userUid ->
            val paidAmount = userBalances[userUid] ?: 0.0
            userBalances[userUid] = paidAmount - sharePerUser
        }

        return calculateDebtsFromBalance(userBalances)
    }

    fun calculateAllDebts(expens: List<Transaction>): List<Debt> {
        val aggregateDebts = mutableListOf<Debt>()
        val userBalances = mutableMapOf<String, Double>()

        // Calculate debts for each expense and aggregate them
        expens.forEach { expense ->
            val debts = calculateDebt(expense)
            aggregateDebts.addAll(debts)
        }

        // Update the user balances based on the debts
        aggregateDebts.forEach { debt ->
            userBalances[debt.debtorUid] =
                userBalances.getOrDefault(debt.debtorUid, 0.0) - debt.amount
            userBalances[debt.creditorUid] =
                userBalances.getOrDefault(debt.creditorUid, 0.0) + debt.amount
        }

        return calculateDebtsFromBalance(userBalances)
    }

    private fun calculateDebtsFromBalance(userBalances: Map<String, Double>): List<Debt> {
        val sortedUsers = userBalances.toList().sortedBy { (_, balance) -> balance }.toMutableList()

        // Settle debts between users
        val debts = mutableListOf<Debt>()
        var leftPointer = 0
        var rightPointer = sortedUsers.size - 1

        while (leftPointer < rightPointer) {
            val (leftUserId, leftBalance) = sortedUsers[leftPointer]
            val (rightUserId, rightBalance) = sortedUsers[rightPointer]

            if (leftBalance == 0.0) {
                leftPointer++
                continue
            }
            if (rightBalance == 0.0) {
                rightPointer--
                continue
            }

            val minAmount = minOf(-leftBalance, rightBalance)

            sortedUsers[leftPointer] = leftUserId to (leftBalance + minAmount)
            sortedUsers[rightPointer] = rightUserId to (rightBalance - minAmount)

            debts.add(Debt(leftUserId, rightUserId, minAmount))

            if (sortedUsers[leftPointer].second == 0.0) leftPointer++
            if (sortedUsers[rightPointer].second == 0.0) rightPointer--
        }

        return debts
    }
}