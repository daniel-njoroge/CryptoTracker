package com.example.cryptotracker.data.repository

import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.model.MarketChartResponse
import com.example.cryptotracker.data.network.CoinGeckoApi
import javax.inject.Inject

class CoinRepository @Inject constructor(
    private val api: CoinGeckoApi
) {
    suspend fun getCoins(): List<Coin> {
        return api.getCoins()
    }

    suspend fun getMarketChart(id: String, days: String): MarketChartResponse {
        return api.getMarketChart(id, days)
    }

    suspend fun searchCoins(query: String): List<Coin> {
        // Local filtering for simplicity
        return getCoins().filter { coin ->
            coin.name.lowercase().contains(query.lowercase()) ||
                    coin.symbol.lowercase().contains(query.lowercase())
        }
    }
}