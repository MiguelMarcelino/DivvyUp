package org.arcane.divvyup.feature.home

import androidx.lifecycle.viewModelScope
import org.arcane.divvyup.base.BaseViewModel
import org.arcane.divvyup.base.HomeNavigationEvent
import org.arcane.divvyup.base.UiEvent
import org.arcane.divvyup.utils.Utils
import org.arcane.divvyup.dbconnector.TransactionConnector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.arcane.divvyup.data.model.Group
import org.arcane.divvyup.data.model.Transaction
import org.arcane.divvyup.dbconnector.GroupConnector
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val transactionConnector: TransactionConnector, private val groupConnector: GroupConnector) : BaseViewModel() {

    override fun onEvent(event: UiEvent) {
        when (event) {
            is HomeUiEvent.OnAddTransactionClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToAddExpense)
                }
            }

            is HomeUiEvent.OnSeeAllClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToSeeAll)
                }
            }
        }
    }

    fun getTransactions(): List<Transaction> {
        // TODO: Get only expenses by user
        return transactionConnector.getItems()
    }

    fun getBalance(list: List<Transaction>): String {
        var balance = 0.0
        for (expense in list) {
            if (expense.type.value() > 0) {
                balance += expense.amount
            } else {
                balance -= expense.amount
            }
        }
        return Utils.formatCurrency(balance)
    }

    fun getTotalAmount(list: List<Transaction>): String {
        var total = 0.0
        for (transaction in list) {
            if (transaction.type.value() < 0) {
                total += transaction.amount
            }
        }

        return Utils.formatCurrency(total)
    }

    fun getTotalIncome(list: List<Transaction>): String {
        var totalIncome = 0.0
        for (transaction in list) {
            if (transaction.type.value() > 0) {
                totalIncome += transaction.amount
            }
        }
        return Utils.formatCurrency(totalIncome)
    }
}

sealed class HomeUiEvent : UiEvent() {
    data object OnAddTransactionClicked : HomeUiEvent()
    data object OnSeeAllClicked : HomeUiEvent()
}
