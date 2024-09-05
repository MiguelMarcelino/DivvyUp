package org.arcane.divvyup.feature.home

import androidx.lifecycle.viewModelScope
import org.arcane.divvyup.base.BaseViewModel
import org.arcane.divvyup.base.HomeNavigationEvent
import org.arcane.divvyup.base.UiEvent
import org.arcane.divvyup.utils.Utils
import org.arcane.divvyup.dbconnector.ExpenseConnector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.arcane.divvyup.data.Expense
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(expenseConnector: ExpenseConnector) : BaseViewModel() {
    val expenses = expenseConnector.getItems()

    override fun onEvent(event: UiEvent) {
        when (event) {
            is HomeUiEvent.OnAddExpenseClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToAddExpense)
                }
            }

            is HomeUiEvent.OnAddIncomeClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToAddIncome)
                }
            }

            is HomeUiEvent.OnSeeAllClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(HomeNavigationEvent.NavigateToSeeAll)
                }
            }
        }
    }

    fun getBalance(list: List<Expense>): String {
        var balance = 0.0
        for (expense in list) {
            if (expense.type == "Income") {
                balance += expense.amount
            } else {
                balance -= expense.amount
            }
        }
        return Utils.formatCurrency(balance)
    }

    fun getTotalExpense(list: List<Expense>): String {
        var total = 0.0
        for (expense in list) {
            if (expense.type != "Income") {
                total += expense.amount
            }
        }

        return Utils.formatCurrency(total)
    }

    fun getTotalIncome(list: List<Expense>): String {
        var totalIncome = 0.0
        for (expense in list) {
            if (expense.type == "Income") {
                totalIncome += expense.amount
            }
        }
        return Utils.formatCurrency(totalIncome)
    }
}

sealed class HomeUiEvent : UiEvent() {
    data object OnAddExpenseClicked : HomeUiEvent()
    data object OnAddIncomeClicked : HomeUiEvent()
    data object OnSeeAllClicked : HomeUiEvent()
}
