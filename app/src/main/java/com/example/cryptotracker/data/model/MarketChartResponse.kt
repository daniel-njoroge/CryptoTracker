package com.example.cryptotracker.data.model

import com.google.gson.annotations.SerializedName

data class MarketChartResponse(
    val prices: List<List<Double>>, // [[timestamp, price], ...]
    @SerializedName("market_caps") val marketCaps: List<List<Double>>, // [[timestamp, market_cap], ...]
    @SerializedName("total_volumes") val totalVolumes: List<List<Double>> // [[timestamp, total_volume], ...]
)