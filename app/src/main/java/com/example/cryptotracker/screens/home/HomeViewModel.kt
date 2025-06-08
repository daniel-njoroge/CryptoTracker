package com.example.cryptotracker.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CoinRepository
) : ViewModel() {
    private val _coins = MutableStateFlow<List<Coin>>(emptyList())
    val coins: StateFlow<List<Coin>> = _coins.asStateFlow()

    init {
        fetchCoins()
    }

    private fun fetchCoins() {
        viewModelScope.launch {
            try {
                _coins.value = repository.getCoins()
            } catch (e: Exception) {
                _coins.value = emptyList() // Handle errors
            }
        }
    }
}