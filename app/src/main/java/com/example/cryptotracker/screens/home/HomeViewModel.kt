package com.example.cryptotracker.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CoinRepository
) : ViewModel() {
    private val _coins = MutableStateFlow<List<Coin>>(emptyList())
    val coins: StateFlow<List<Coin>> = _coins

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchCoinsPeriodically()
    }

    private fun fetchCoinsPeriodically() {
        viewModelScope.launch {
            while (isActive) {
                try {
                    val coinList = repository.getCoins()
                    _error.value = null
                    _coins.value = if (_searchQuery.value.isEmpty()) {
                        coinList.filter { it.name != null && it.symbol != null }
                    } else {
                        coinList.filter { coin ->
                            val query = _searchQuery.value.lowercase()
                            coin.name?.lowercase()?.contains(query) == true ||
                                    coin.symbol?.lowercase()?.contains(query) == true
                        }
                    }
                } catch (e: Exception) {
                    _coins.value = emptyList()
                    _error.value = "Failed to load coins: ${e.message}"
                }
                delay(120_000) // Refresh every 2 minutes to reduce load
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            try {
                val coinList = repository.getCoins()
                _error.value = null
                _coins.value = if (query.isEmpty()) {
                    coinList.filter { it.name != null && it.symbol != null }
                } else {
                    coinList.filter { coin ->
                        val queryLower = query.lowercase()
                        coin.name?.lowercase()?.contains(queryLower) == true ||
                                coin.symbol?.lowercase()?.contains(queryLower) == true
                    }
                }
            } catch (e: Exception) {
                _coins.value = emptyList()
                _error.value = "Failed to load coins: ${e.message}"
            }
        }
    }
}