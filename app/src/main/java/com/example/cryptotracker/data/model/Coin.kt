package com.example.cryptotracker.data.model

import com.google.gson.annotations.SerializedName

data class Coin(
    val id: String, // e.g., "bitcoin"
    val symbol: String, // e.g., "btc"
    val name: String, // e.g., "Bitcoin"
    val image: String, // URL to coin logo
    @SerializedName("current_price") val currentPrice: Double, // Current price in USD
    @SerializedName("market_cap") val marketCap: Long, // Market capitalization
    @SerializedName("total_volume") val totalVolume: Long, // 24h trading volume
    @SerializedName("price_change_percentage_24h") val priceChange24h: Double // 24h price change %
)