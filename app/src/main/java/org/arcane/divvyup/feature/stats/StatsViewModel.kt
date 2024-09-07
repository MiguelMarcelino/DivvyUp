package org.arcane.divvyup.feature.stats

import android.R.attr
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.utils.EntryXComparator
import dagger.hilt.android.lifecycle.HiltViewModel
import org.arcane.divvyup.base.BaseViewModel
import org.arcane.divvyup.base.UiEvent
import org.arcane.divvyup.data.Expense
import org.arcane.divvyup.dbconnector.ExpenseConnector
import java.util.Collections
import javax.inject.Inject


@HiltViewModel
class StatsViewModel @Inject constructor(private val expenseConnector: ExpenseConnector) : BaseViewModel() {
    fun getEntriesForChart(entries: List<Expense>): List<Entry> {
        val list = mutableListOf<Entry>()
        for (entry in entries) {
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

    fun getExpenses(): List<Expense> {
        // TODO: Get only expenses by user
        return expenseConnector.getItems()
    }

    fun getTopExpenses(): List<Expense> {
        // TODO: Get top expenses by user
        return expenseConnector.getItems().sortedBy { it.amount }.take(5)
    }
}

