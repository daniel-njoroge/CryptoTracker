package com.example.cryptotracker.screens.details

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun DetailsScreen(coinId: String, viewModel: DetailsViewModel = hiltViewModel()) {
    viewModel.fetchChartData(coinId)
    val chartData = viewModel.chartData.collectAsState().value

    if (chartData == null) {
        Text(
            text = "Loading chart data...",
            modifier = Modifier.padding(16.dp)
        )
    } else {
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    setBackgroundColor(Color.WHITE)
                    description.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    setPinchZoom(true)
                }
            },
            update = { chart ->
                val entries = chartData.prices.mapIndexed { index, price ->
                    Entry(index.toFloat(), price[1].toFloat())
                }
                val dataSet = LineDataSet(entries, "Price").apply {
                    color = Color.BLUE
                    setDrawCircles(false)
                    lineWidth = 2f
                }
                chart.data = LineData(dataSet)
                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}