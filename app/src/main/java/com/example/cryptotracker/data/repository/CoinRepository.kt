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
            // Don't use interval parameter as it can cause 400 errors
            val response = api.getMarketChart(id, "usd", days)
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
                
                // Log more details about the error
                when {
                    e.message?.contains("400") == true -> {
                        Log.e(TAG, "Bad Request (400): Check API parameters")
                    }
                    e.message?.contains("429") == true -> {
                        Log.w(TAG, "Rate limit (429): Too many requests")
                    }
                    e.message?.contains("404") == true -> {
                        Log.e(TAG, "Not Found (404): Resource doesn't exist")
                    }
                }
                
                if ((e.message?.contains("429") == true || e.message?.contains("500") == true) && attempt < maxRetries) {
                    attempt++
                    Log.d(TAG, "Retrying ($attempt/$maxRetries) after ${delayMs}ms")
                    delay(delayMs)
                    delayMs *= 2 // Exponential backoff: 1s, 2s, 4s, etc.
                } else {
                    throw e
                }
            }
        }
    }
}