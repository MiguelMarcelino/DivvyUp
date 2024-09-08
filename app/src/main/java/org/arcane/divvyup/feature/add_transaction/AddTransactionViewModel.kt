package org.arcane.divvyup.feature.add_transaction

import androidx.lifecycle.viewModelScope
import org.arcane.divvyup.base.AddTransactionNavigationEvent
import org.arcane.divvyup.base.BaseViewModel
import org.arcane.divvyup.base.NavigationEvent
import org.arcane.divvyup.base.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.arcane.divvyup.data.model.RecurrentTransaction
import org.arcane.divvyup.data.model.Transaction
import org.arcane.divvyup.dbconnector.RecurrentTransactionConnector
import org.arcane.divvyup.dbconnector.TransactionConnector
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(private val transactionConnector: TransactionConnector, private val recurrentTransactionConnector: RecurrentTransactionConnector) :
    BaseViewModel() {


    private fun addTransaction(transactionEntity: Transaction): Boolean {
        return try {
            transactionConnector.addItem(transactionEntity)
            true
        } catch (ex: Throwable) {
            false
        }
    }

    private fun addRecurrentTransaction(recurrentTransaction: RecurrentTransaction): Boolean {
        return try {
            recurrentTransactionConnector.addItem(recurrentTransaction)
            true
        } catch (ex: Throwable) {
            false
        }
    }

    override fun onEvent(event: UiEvent) {
        when (event) {
            is AddTransactionUiEvent.OnAddTransactionClicked -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        val result = addTransaction(event.transactionEntity)
                        val resultRecurrent = when(event.recurrentTransaction) {
                            null -> true
                            else -> addRecurrentTransaction(event.recurrentTransaction)
                        }
                        if (result && resultRecurrent) {
                            _navigationEvent.emit(NavigationEvent.NavigateBack)
                        }
                    }
                }
            }

            is AddTransactionUiEvent.OnBackPressed -> {
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.NavigateBack)
                }
            }

            is AddTransactionUiEvent.OnMenuClicked -> {
                viewModelScope.launch {
                    _navigationEvent.emit(AddTransactionNavigationEvent.MenuOpenedClicked)
                }
            }
        }
    }
}

sealed class AddTransactionUiEvent : UiEvent() {
    data class OnAddTransactionClicked(
        val transactionEntity: Transaction,
        val recurrentTransaction: RecurrentTransaction?
    ) : AddTransactionUiEvent()

    data object OnBackPressed : AddTransactionUiEvent()
    data object OnMenuClicked : AddTransactionUiEvent()
}


