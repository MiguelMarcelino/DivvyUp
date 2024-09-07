package org.arcane.divvyup.feature.add_expense

import androidx.lifecycle.viewModelScope
import org.arcane.divvyup.base.AddExpenseNavigationEvent
import org.arcane.divvyup.base.BaseViewModel
import org.arcane.divvyup.base.NavigationEvent
import org.arcane.divvyup.base.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.arcane.divvyup.data.Expense
import org.arcane.divvyup.dbconnector.ExpenseConnector
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(private val expenseConnector: ExpenseConnector) : BaseViewModel() {


    private fun addExpense(expenseEntity: Expense): Boolean {
        return try {
            expenseConnector.addItem(expenseEntity)
            true
        } catch (ex: Throwable) {
            false
        }
    }

    override fun onEvent(event: UiEvent) {
        when (event) {
            is AddExpenseUiEvent.OnAddExpenseClicked -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        val result = addExpense(event.expenseEntity)
                        if (result) {
                            _navigationEvent.emit(NavigationEvent.NavigateBack)
                        }
                    }
                }
            }

            is AddExpenseUiEvent.OnBackPressed -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.NavigateBack)
                }
            }

            is AddExpenseUiEvent.OnMenuClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(AddExpenseNavigationEvent.MenuOpenedClicked)
                }
            }
        }
    }
}

sealed class AddExpenseUiEvent : UiEvent() {
    data class OnAddExpenseClicked(val expenseEntity: Expense) : AddExpenseUiEvent()
    data object OnBackPressed : AddExpenseUiEvent()
    data object OnMenuClicked : AddExpenseUiEvent()
}


