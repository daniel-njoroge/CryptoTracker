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
                
                // Validate coinId
                if (coinId.isBlank() || coinId == "null") {
                    _error.value = "Invalid coin ID provided"
                    _chartData.value = null
                    return@launch
                }
                
                val response = repository.getMarketChart(coinId, days)
                Log.d(TAG, "Fetched chart data for $coinId, days=$days: prices count=${response.prices.size}")
                
                // Validate response data
                if (response.prices.isEmpty()) {
                    _error.value = "No chart data available for this coin"
                    _chartData.value = null
                } else {
                    // Validate each price point
                    val validPrices = response.prices.filter { pricePoint ->
                        pricePoint.size >= 2 && pricePoint[1] > 0
                    }
                    
                    if (validPrices.isEmpty()) {
                        _error.value = "Chart data contains invalid price information"
                        _chartData.value = null
                    } else {
                        val filteredResponse = response.copy(prices = validPrices)
                        _chartData.value = filteredResponse
                        Log.d(TAG, "Successfully loaded ${validPrices.size} valid price points")
                    }
                }
            } catch (e: Exception) {
                _chartData.value = null
                val errorMessage = when {
                    e.message?.contains("404") == true -> "Coin not found"
                    e.message?.contains("429") == true -> "Too many requests, please wait"
                    e.message?.contains("timeout") == true -> "Request timed out, please retry"
                    else -> "Failed to load chart data: ${e.message}"
                }
                _error.value = errorMessage
                Log.e(TAG, errorMessage, e)
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