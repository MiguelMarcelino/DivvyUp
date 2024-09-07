package org.arcane.divvyup.feature.add_transaction

import androidx.lifecycle.viewModelScope
import org.arcane.divvyup.base.AddExpenseNavigationEvent
import org.arcane.divvyup.base.BaseViewModel
import org.arcane.divvyup.base.NavigationEvent
import org.arcane.divvyup.base.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.arcane.divvyup.data.Transaction
import org.arcane.divvyup.dbconnector.TransactionConnector
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(private val transactionConnector: TransactionConnector) : BaseViewModel() {


    private fun addExpense(transactionEntity: Transaction): Boolean {
        return try {
            transactionConnector.addItem(transactionEntity)
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
                        val result = addExpense(event.transactionEntity)
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
    data class OnAddExpenseClicked(val transactionEntity: Transaction) : AddExpenseUiEvent()
    data object OnBackPressed : AddExpenseUiEvent()
    data object OnMenuClicked : AddExpenseUiEvent()
}


