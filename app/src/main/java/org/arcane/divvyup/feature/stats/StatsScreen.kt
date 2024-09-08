package org.arcane.divvyup.feature.stats

import android.view.LayoutInflater
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.github.mikephil.charting.animation.Easing
import org.arcane.divvyup.R
import org.arcane.divvyup.utils.Utils
import org.arcane.divvyup.feature.home.TransactionList
import org.arcane.divvyup.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun StatsScreen(navController: NavController, viewModel: StatsViewModel = hiltViewModel()) {
    Scaffold(topBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier.align(
                    Alignment.CenterStart
                ).clickable {
                    navController.navigateUp()
                },
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
            )
            TextView(
                text = "Your Expenses",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
            Image(
                painter = painterResource(id = R.drawable.dots_menu),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black)
            )
        }
    }) {
        val dataState = viewModel.getTransactions()
        val topExpense = viewModel.getTopExpenses()
        Column(modifier = Modifier.padding(it)) {
            val entries = viewModel.getEntriesForChart(dataState)
            LineChart(entries = entries)
            Spacer(modifier = Modifier.height(16.dp))
            TransactionList(Modifier, list = topExpense, "Top Spending", onSeeAllClicked = {})
        }
    }
}

@Composable
fun LineChart(entries: List<Entry>) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            val view = LayoutInflater.from(context).inflate(R.layout.stats_line_chart, null)
            view
        }, modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) { view ->
        val lineChart = view.findViewById<LineChart>(R.id.lineChart)

        val dataSet = LineDataSet(entries, "Transactions").apply {
            color = android.graphics.Color.parseColor("#FF2F7E79")
            valueTextColor = android.graphics.Color.BLACK
            lineWidth = 3f
            axisDependency = YAxis.AxisDependency.RIGHT
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextSize = 14f
            valueTextColor = android.graphics.Color.parseColor("#FF2F7E79")

            // Gradient fill below the line
            setDrawFilled(true)
            val drawable = ContextCompat.getDrawable(context, R.drawable.char_gradient)
            drawable?.let {
                fillDrawable = it
            }

            // Modern dot markers on data points
            setDrawCircles(true)
            circleRadius = 6f
            circleHoleRadius = 4f
            circleHoleColor = android.graphics.Color.WHITE
            setCircleColor(android.graphics.Color.parseColor("#FF2F7E79"))
        }

        // General chart styling
        lineChart.apply {
            setTouchEnabled(true)
            setPinchZoom(true)
            setScaleEnabled(true)
            legend.isEnabled = false
            description.isEnabled = false
            animateX(1500, Easing.EaseInOutQuad) // Smooth animation
            setViewPortOffsets(60f, 20f, 60f, 100f) // Adjust for better view
        }

        // X-axis styling
        lineChart.xAxis.apply {
            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return Utils.formatDateForChart(value.toLong())
                }
            }
            position = XAxis.XAxisPosition.BOTTOM
            textColor = android.graphics.Color.DKGRAY
            textSize = 12f
            granularity = 1f // Ensures that the labels are not repeated
            isGranularityEnabled = true
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }

        lineChart.data = com.github.mikephil.charting.data.LineData(dataSet)
        lineChart.axisLeft.isEnabled = false
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.axisRight.isEnabled = false
        lineChart.axisRight.setDrawGridLines(false)
        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.invalidate()
    }
}