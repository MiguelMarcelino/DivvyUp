package org.arcane.divvyup.feature.stats

import org.arcane.divvyup.base.BaseViewModel
import org.arcane.divvyup.base.UiEvent
import org.arcane.divvyup.utils.Utils
import org.arcane.divvyup.data.dao.ExpenseDao
import org.arcane.divvyup.data.model.ExpenseSummary
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(val dao: ExpenseDao) : BaseViewModel() {
    val entries = dao.getAllExpenseByDate()
    val topEntries = dao.getTopExpenses()
    fun getEntriesForChart(entries: List<ExpenseSummary>): List<Entry> {
        val list = mutableListOf<Entry>()
        for (entry in entries) {
            val formattedDate = Utils.getMillisFromDate(entry.date)
            list.add(Entry(formattedDate.toFloat(), entry.total_amount.toFloat()))
        }
        return list
    }

    override fun onEvent(event: UiEvent) {
    }
}

