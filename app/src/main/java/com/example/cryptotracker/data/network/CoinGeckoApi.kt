package com.example.cryptotracker.data.network

import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.model.MarketChartResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApi {
    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false,
        @Header("x-cg-demo-api-key") apiKey: String? = null
    ): List<Coin>

    @GET("coins/{id}/market_chart")
    suspend fun getMarketChart(
        @Path("id") id: String,
        @Query("vs_currency") currency: String = "usd",
        @Query("days") days: String = "7",
        @Header("x-cg-demo-api-key") apiKey: String? = null
    ): MarketChartResponse

    @GET("coins/{id}")
    suspend fun getCoinDetails(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = true,
        @Query("community_data") communityData: Boolean = false,
        @Query("developer_data") developerData: Boolean = false,
        @Query("sparkline") sparkline: Boolean = false,
        @Header("x-cg-demo-api-key") apiKey: String? = null
    ): Coin
}