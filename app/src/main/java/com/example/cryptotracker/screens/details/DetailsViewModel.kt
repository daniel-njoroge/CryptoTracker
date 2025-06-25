package com.example.cryptotracker.screens.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.model.MarketChartResponse
import com.example.cryptotracker.data.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: CoinRepository
) : ViewModel() {
    private val _chartData = MutableStateFlow<MarketChartResponse?>(null)
    val chartData: StateFlow<MarketChartResponse?> = _chartData

    private val _selectedRange = MutableStateFlow("7")
    val selectedRange: StateFlow<String> = _selectedRange

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _coin = MutableStateFlow<Coin?>(null)
    val coin: StateFlow<Coin?> = _coin

    private val TAG = "DetailsViewModel"

    fun fetchChartData(coinId: String, days: String = "7") {
        _selectedRange.value = days
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                Log.d(TAG, "Fetching chart data for coinId='$coinId', days='$days'")
                
                // Test with a known good coin ID first if this is an unknown coin
                val testId = if (coinId.isBlank() || coinId == "null") {
                    Log.w(TAG, "Invalid coinId '$coinId', using 'bitcoin' as fallback")
                    "bitcoin"
                } else {
                    coinId
                }
                
                val response = repository.getMarketChart(testId, days)
                Log.d(TAG, "Fetched chart data for $testId, days=$days: prices count=${response.prices.size}")
                
                if (response.prices.isEmpty()) {
                    _error.value = "No chart data available for $testId"
                    _chartData.value = null
                } else {
                    _chartData.value = response
                    Log.d(TAG, "Successfully loaded ${response.prices.size} price points")
                }
            } catch (e: Exception) {
                _chartData.value = null
                val errorMessage = "Failed to load chart data: ${e.message}"
                _error.value = errorMessage
                Log.e(TAG, errorMessage, e)
                
                // Print stack trace for debugging
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchCoinData(coinId: String) {
        viewModelScope.launch {
            try {
                // Only get basic coin data from the coins list (more reliable)
                val coins = repository.getCoins()
                val basicCoin = coins.find { it.id == coinId }
                
                if (basicCoin != null) {
                    _coin.value = basicCoin
                    Log.d(TAG, "Found coin data for $coinId: ${basicCoin.name}")
                } else {
                    _error.value = "Coin $coinId not found"
                    Log.w(TAG, "Coin $coinId not found in coins list")
                }
            } catch (e: Exception) {
                _error.value = "Failed to load coin data: ${e.message}"
                _coin.value = null
                Log.e(TAG, "Error loading coin data: ${e.message}", e)
            }
        }
    }

    fun updateRange(days: String) {
        _selectedRange.value = days
    }
}