package com.example.cryptotracker.screens.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.ui.theme.Primary
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DetailsScreen(coinId: String, viewModel: DetailsViewModel = hiltViewModel()) {
    val chartData = viewModel.chartData.collectAsState().value
    val selectedRange = viewModel.selectedRange.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.error.collectAsState().value
    val coin = viewModel.coin.collectAsState().value

    // Fetch chart and coin data
    viewModel.fetchChartData(coinId)
    viewModel.fetchCoinData(coinId)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Coin Info
            if (coin != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = coin.image,
                        contentDescription = "${coin.name ?: "Coin"} logo",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(end = 16.dp)
                    )
                    Column {
                        Text(
                            text = coin.name ?: "Unknown",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = coin.currentPrice?.let { "$${String.format("%.2f", it)}" } ?: "N/A",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = coin.priceChange24h?.let { "24h: ${String.format("%.2f", it)}%" } ?: "N/A",
                            style = MaterialTheme.typography.bodyLarge,
                            color = coin.priceChange24h?.let { if (it >= 0) androidx.compose.ui.graphics.Color(0xFF4CAF50) else androidx.compose.ui.graphics.Color(0xFFF44336) } ?: MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = coin.high24h?.let { "24h High: $${String.format("%.2f", it)}" } ?: "N/A",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = coin.low24h?.let { "24h Low: $${String.format("%.2f", it)}" } ?: "N/A",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Time range buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("1" to "1D", "7" to "7D", "30" to "1M").forEach { (days, label) ->
                    Button(
                        onClick = {
                            viewModel.updateRange(days)
                            viewModel.fetchChartData(coinId, days)
                        },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        enabled = selectedRange != days,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedRange == days) Primary else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(text = label, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            // Loading, Error, and Chart States
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp),
                    color = Primary
                )
            }
            AnimatedVisibility(
                visible = error != null && !isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            AnimatedVisibility(
                visible = chartData != null && !isLoading && error == null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                chartData?.let { data ->
                    if (data.prices.isEmpty()) {
                        Text(
                            text = "No price data available",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        AndroidView(
                            factory = { context ->
                                LineChart(context).apply {
                                    setBackgroundColor(androidx.compose.ui.graphics.Color.White.value.toInt())
                                    description.isEnabled = false
                                    setTouchEnabled(true)
                                    isDragEnabled = true
                                    setScaleEnabled(true)
                                    setPinchZoom(true)
                                    xAxis.apply {
                                        position = XAxis.XAxisPosition.BOTTOM
                                        setDrawGridLines(false)
                                        textColor = androidx.compose.ui.graphics.Color.Black.value.toInt()
                                    }
                                    axisLeft.textColor = androidx.compose.ui.graphics.Color.Black.value.toInt()
                                    axisRight.isEnabled = false
                                    legend.textColor = androidx.compose.ui.graphics.Color.Black.value.toInt()
                                }
                            },
                            update = { chart ->
                                val entries = data.prices.mapIndexed { index, price ->
                                    Entry(index.toFloat(), price[1].toFloat())
                                }
                                val dataSet = LineDataSet(entries, "Price (USD)").apply {
                                    color = Primary.value.toInt()
                                    setDrawCircles(false)
                                    lineWidth = 2f
                                    setDrawValues(false)
                                    setDrawFilled(true)
                                    fillColor = Primary.value.toInt()
                                }
                                chart.data = LineData(dataSet)
                                chart.invalidate()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}