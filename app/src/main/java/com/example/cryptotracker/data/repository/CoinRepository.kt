package com.example.cryptotracker.data.repository

import android.util.Log
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.model.MarketChartResponse
import com.example.cryptotracker.data.network.CoinGeckoApi
import kotlinx.coroutines.delay
import javax.inject.Inject

class CoinRepository @Inject constructor(
    private val api: CoinGeckoApi
) {
    private val maxRetries = 3
    private val initialDelayMs = 1000L // 1 second initial delay
    private val TAG = "CoinRepository"

    suspend fun getCoins(): List<Coin> {
        return withRetry { api.getCoins() }.filterNotNull()
    }

    suspend fun getMarketChart(id: String, days: String): MarketChartResponse {
        return withRetry {
            val response = api.getMarketChart(id, days, interval = when (days) {
                "1" -> "hourly"
                else -> "daily"
            })
            Log.d(TAG, "MarketChart for $id, days=$days: prices=${response.prices.size}, market_caps=${response.marketCaps.size}, total_volumes=${response.totalVolumes.size}")
            response
        }
    }

    suspend fun getCoinById(id: String): Coin? {
        return withRetry { api.getCoinDetails(id) }
    }

    suspend fun searchCoins(query: String): List<Coin> {
        val coins = withRetry { api.getCoins() }.filterNotNull()
        return coins.filter { coin ->
            coin.name?.lowercase()?.contains(query.lowercase()) == true ||
                    coin.symbol?.lowercase()?.contains(query.lowercase()) == true
        }
    }

    private suspend fun <T> withRetry(block: suspend () -> T): T {
        var attempt = 0
        var delayMs = initialDelayMs
        while (true) {
            try {
                return block()
            } catch (e: Exception) {
                Log.e(TAG, "API call failed: ${e.message}", e)
                if (e.message?.contains("429") == true && attempt < maxRetries) {
                    attempt++
                    Log.d(TAG, "Rate limit hit, retrying ($attempt/$maxRetries) after ${delayMs}ms")
                    delay(delayMs)
                    delayMs *= 2 // Exponential backoff: 1s, 2s, 4s, etc.
                } else {
                    throw e
                }
            }
        }
    }
}