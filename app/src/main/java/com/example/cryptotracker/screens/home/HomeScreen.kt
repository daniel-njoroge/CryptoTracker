package com.example.cryptotracker.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptotracker.data.model.Coin

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val coins = viewModel.coins.collectAsState().value
    if (coins.isEmpty()) {
        Text(
            text = "Loading or no data available...",
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyColumn {
            items(coins) { coin ->
                CoinCard(coin = coin, onClick = {
                    navController.navigate("details/${coin.id}")
                })
            }
        }
    }
}

@Composable
fun CoinCard(coin: Coin, onClick: () -> Unit) {
    Text(
        text = "${coin.name} (${coin.symbol}): $${coin.currentPrice}",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    )
}