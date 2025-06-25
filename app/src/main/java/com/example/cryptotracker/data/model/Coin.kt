package com.example.cryptotracker.data.model

import com.google.gson.annotations.SerializedName

data class Coin(
    val id: String?, // e.g., "bitcoin"
    val symbol: String?, // e.g., "btc"
    val name: String?, // e.g., "Bitcoin"
    val image: String?, // URL to coin logo
    @SerializedName("current_price") val currentPrice: Double?, // Current price in USD
    @SerializedName("market_cap") val marketCap: Long?, // Market capitalization
    @SerializedName("total_volume") val totalVolume: Long?, // 24h trading volume
    @SerializedName("price_change_percentage_24h") val priceChange24h: Double?, // 24h price change %
    @SerializedName("high_24h") val high24h: Double?, // 24h high price
    @SerializedName("low_24h") val low24h: Double?, // 24h low price
    @SerializedName("market_cap_rank") val marketCapRank: Int?, // Market cap ranking
    @SerializedName("circulating_supply") val circulatingSupply: Double?, // Circulating supply
    @SerializedName("total_supply") val totalSupply: Double?, // Total supply
    @SerializedName("max_supply") val maxSupply: Double?, // Maximum supply
    @SerializedName("ath") val allTimeHigh: Double?, // All-time high
    @SerializedName("atl") val allTimeLow: Double?, // All-time low
    @SerializedName("price_change_percentage_7d_in_currency") val priceChange7d: Double?, // 7d price change %
    @SerializedName("price_change_percentage_30d_in_currency") val priceChange30d: Double? // 30d price change %
)
