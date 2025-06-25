package com.example.cryptotracker.screens.details


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cryptotracker.ui.theme.*
import com.example.cryptotracker.ui.components.PriceChart

@Composable
fun DetailsScreen(
    coinId: String, 
    navController: NavController,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val chartData by viewModel.chartData.collectAsState()
    val selectedRange by viewModel.selectedRange.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val coin by viewModel.coin.collectAsState()

    // Fetch the necessary data only once
    LaunchedEffect(coinId) {
        viewModel.fetchCoinData(coinId)
        viewModel.fetchChartData(coinId, selectedRange)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(coin?.name ?: "Coin Details") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Coin Info
            coin?.let { currentCoin ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = currentCoin.image,
                        contentDescription = "${currentCoin.name ?: "Coin"} logo",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(end = 16.dp)
                    )
                    Column {
                        Text(
                            text = currentCoin.name ?: "Unknown",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = currentCoin.currentPrice?.let { "$${String.format("%.2f", it)}" } ?: "N/A",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = currentCoin.priceChange24h?.let { "24h: ${String.format("%.2f", it)}%" } ?: "N/A",
                            style = MaterialTheme.typography.bodyLarge,
                            color = currentCoin.priceChange24h?.let { if (it >= 0) SuccessGreen else ErrorRed } ?: MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = currentCoin.high24h?.let { "24h High: $${String.format("%.2f", it)}" } ?: "N/A",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currentCoin.low24h?.let { "24h Low: $${String.format("%.2f", it)}" } ?: "N/A",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                // Market Statistics Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Market Statistics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                MarketStatItem(
                                    label = "Market Cap Rank",
                                    value = currentCoin.marketCapRank?.let { "#$it" } ?: "N/A"
                                )
                                MarketStatItem(
                                    label = "Market Cap",
                                    value = currentCoin.marketCap?.let { formatLargeNumber(it) } ?: "N/A"
                                )
                                MarketStatItem(
                                    label = "24h Volume",
                                    value = currentCoin.totalVolume?.let { formatLargeNumber(it) } ?: "N/A"
                                )
                                MarketStatItem(
                                    label = "All Time High",
                                    value = currentCoin.allTimeHigh?.let { "$${String.format("%.2f", it)}" } ?: "N/A"
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                MarketStatItem(
                                    label = "Circulating Supply",
                                    value = currentCoin.circulatingSupply?.let { formatLargeNumber(it.toLong()) } ?: "N/A"
                                )
                                MarketStatItem(
                                    label = "Total Supply",
                                    value = currentCoin.totalSupply?.let { formatLargeNumber(it.toLong()) } ?: "N/A"
                                )
                                MarketStatItem(
                                    label = "Max Supply",
                                    value = currentCoin.maxSupply?.let { formatLargeNumber(it.toLong()) } ?: "∞"
                                )
                                MarketStatItem(
                                    label = "All Time Low",
                                    value = currentCoin.allTimeLow?.let { "$${String.format("%.2f", it)}" } ?: "N/A"
                                )
                            }
                        }
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
                    FilterChip(
                        onClick = {
                            if (selectedRange != days) {
                                viewModel.updateRange(days)
                                viewModel.fetchChartData(coinId, days)
                            }
                        },
                        label = { Text(text = label) },
                        selected = selectedRange == days,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // Chart section - simplified state management
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Primary,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading chart data...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                error != null -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = ErrorRed.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⚠️",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error ?: "Unknown error",
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.fetchChartData(coinId, selectedRange) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary
                                )
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                chartData != null -> {
                    PriceChart(
                        chartData = chartData,
                        selectedRange = selectedRange,
                        isLoading = isLoading,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                else -> {
                    // Initial empty state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chart will appear here",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarketStatItem(label: String, value: String) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun formatLargeNumber(number: Long): String {
    return when {
        number >= 1_000_000_000_000 -> "$${String.format("%.1f", number / 1_000_000_000_000.0)}T"
        number >= 1_000_000_000 -> "$${String.format("%.1f", number / 1_000_000_000.0)}B"
        number >= 1_000_000 -> "$${String.format("%.1f", number / 1_000_000.0)}M"
        number >= 1_000 -> "$${String.format("%.1f", number / 1_000.0)}K"
        else -> "$$number"
    }
}
