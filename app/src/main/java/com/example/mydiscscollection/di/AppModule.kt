package com.example.mydiscscollection.di

import android.util.Log
import com.example.mydiscscollection.BuildConfig
import com.example.mydiscscollection.data.remote.DiscogsApiService
import com.example.mydiscscollection.data.repository.ArtistRepositoryImpl
import com.example.mydiscscollection.domain.repository.ArtistRepository
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api.discogs.com/"
    private const val CONSUMER_KEY = "TsTKkSqWNrKtYaEiNUrr"
    private const val CONSUMER_SECRET = "YoSgsLlLBehVrWLJRsXjMFVhjcIYcvhA"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader(
                        "Authorization",
                        "Discogs key=$CONSUMER_KEY, secret=$CONSUMER_SECRET"
                    )
                    .addHeader("User-Agent", "DiscogsApp/1.0")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(
                HttpLoggingInterceptor { message -> Log.d("DiscogsApi", message) }.apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideDisCogsApiService(retrofit: Retrofit): DiscogsApiService =
        retrofit.create(DiscogsApiService::class.java)

    @Provides
    @Singleton
    fun provideArtisRepository(
        apiService: DiscogsApiService,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): ArtistRepository =
        ArtistRepositoryImpl(
            apiService = apiService,
            ioDispatcher = ioDispatcher
        )
}
