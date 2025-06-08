package com.example.cryptotracker.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun fetchChartData(coinId: String, days: String = "7") {
        viewModelScope.launch {
            try {
                val response = repository.getMarketChart(coinId, days)
                _chartData.value = response
            } catch (e: Exception) {
                _chartData.value = null
            }
        }
    }
}