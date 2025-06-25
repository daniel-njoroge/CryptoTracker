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
                val response = repository.getMarketChart(coinId, days)
                Log.d(TAG, "Fetched chart data for $coinId, days=$days: ${response.prices}")
                if (response.prices.isEmpty()) {
                    _error.value = "No chart data available for $coinId"
                    _chartData.value = null
                } else {
                    _chartData.value = response
                }
            } catch (e: Exception) {
                _chartData.value = null
                _error.value = "Failed to load chart data: ${e.message}"
                Log.e(TAG, "Error loading chart data: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchCoinData(coinId: String) {
        viewModelScope.launch {
            try {
                // Don't set loading again if we're already loading chart data
                if (!_isLoading.value) _isLoading.value = true
                _error.value = null
                
                // First try to get basic coin data from the coins list
                val coins = repository.getCoins()
                val basicCoin = coins.find { it.id == coinId }
                
                if (basicCoin != null) {
                    _coin.value = basicCoin
                    Log.d(TAG, "Found basic coin data for $coinId: ${basicCoin.name}")
                } else {
                    // If not found in basic list, try detailed API
                    val detailedCoin = repository.getCoinById(coinId)
                    if (detailedCoin != null) {
                        _coin.value = detailedCoin
                        Log.d(TAG, "Found detailed coin data for $coinId: ${detailedCoin.name}")
                    } else {
                        _error.value = "Coin $coinId not found"
                        Log.w(TAG, "Coin $coinId not found in both basic and detailed APIs")
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to load coin data: ${e.message}"
                _coin.value = null
                Log.e(TAG, "Error loading coin data: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateRange(days: String) {
        _selectedRange.value = days
    }
}