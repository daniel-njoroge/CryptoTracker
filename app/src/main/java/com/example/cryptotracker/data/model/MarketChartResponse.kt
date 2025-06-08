package com.example.cryptotracker.data.model

data class MarketChartResponse(
    val prices: List<List<Double>> // [[timestamp, price], ...]
)