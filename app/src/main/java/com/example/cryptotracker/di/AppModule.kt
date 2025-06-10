package com.example.cryptotracker.di

import com.example.cryptotracker.data.network.CoinGeckoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Named("ApiKey")
    fun provideApiKey(): String? = null

    @Provides
    @Singleton
    fun provideOkHttpClient(@Named("ApiKey") apiKey: String?): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val retryInterceptor = Interceptor { chain ->
            var request = chain.request()
            var response = chain.proceed(request)
            var attempts = 0
            val maxAttempts = 3
            while (response.code == 429 && attempts < maxAttempts) {
                response.close()
                val delayMs = (1000L * Math.pow(2.0, attempts.toDouble())).toLong() // 1s, 2s, 4s
                Thread.sleep(delayMs)
                attempts++
                request = request.newBuilder().build()
                response = chain.proceed(request)
            }
            response
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(retryInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCoinGeckoApi(retrofit: Retrofit): CoinGeckoApi {
        return retrofit.create(CoinGeckoApi::class.java)
    }
}