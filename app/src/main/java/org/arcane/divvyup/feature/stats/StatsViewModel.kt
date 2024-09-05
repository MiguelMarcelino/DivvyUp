package org.arcane.divvyup.feature.stats

import org.arcane.divvyup.base.BaseViewModel
import org.arcane.divvyup.base.UiEvent
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import org.arcane.divvyup.data.Expense
import org.arcane.divvyup.dbconnector.ExpenseConnector
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(expenseConnector: ExpenseConnector) : BaseViewModel() {
    val entries = expenseConnector.getItems()
    // TODO: This is temporary. We need to get the top 5 expenses from the database
    val topEntries = expenseConnector.getItems().sortedBy { it.amount }.take(5)
    fun getEntriesForChart(entries: List<Expense>): List<Entry> {
        val list = mutableListOf<Entry>()
        for (entry in entries) {
            val formattedDate = entry.createdAt.nanoseconds
            list.add(Entry(formattedDate.toFloat(), entry.amount.toFloat()))
        }
        return list
    }

    override fun onEvent(event: UiEvent) {
    }
}

