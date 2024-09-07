package org.arcane.divvyup.feature.stats

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.EntryXComparator
import dagger.hilt.android.lifecycle.HiltViewModel
import org.arcane.divvyup.base.BaseViewModel
import org.arcane.divvyup.base.UiEvent
import org.arcane.divvyup.data.Transaction
import org.arcane.divvyup.dbconnector.TransactionConnector
import java.util.Collections
import javax.inject.Inject


@HiltViewModel
class StatsViewModel @Inject constructor(private val transactionConnector: TransactionConnector) : BaseViewModel() {
    fun getEntriesForChart(entries: List<Transaction>): List<Entry> {
        val list = mutableListOf<Entry>()
        entries.filter { it.type.value() < 0 }.forEach{entry ->
            val formattedDate = entry.createdAt.nanoseconds
            list.add(Entry(formattedDate.toFloat(), entry.amount.toFloat()))
        }

        // We need to sort the list, otherwise the library tries to create a list
        // with an negative size
        // Issue link: https://github.com/PhilJay/MPAndroidChart/issues/2074#issuecomment-238458565
        Collections.sort(list, EntryXComparator())
        return list
    }

    override fun onEvent(event: UiEvent) {
    }

    fun getTransactions(): List<Transaction> {
        // TODO: Get only expenses by user
        return transactionConnector.getItems()
    }

    fun getTopExpenses(): List<Transaction> {
        // TODO: Get top expenses by user
        return transactionConnector.getItems().filter { it.type.value() < 0 }.sortedBy { it.amount }.take(5)
    }
}

