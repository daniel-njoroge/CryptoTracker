package com.example.cryptotracker.di

import com.example.cryptotracker.data.network.CoinGeckoApi
import com.example.cryptotracker.data.network.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideCoinGeckoApi(): CoinGeckoApi = RetrofitClient.api
}