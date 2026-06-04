package com.mvl.app.di

import com.google.gson.Gson
import com.mvl.app.data.api.AqiApi
import com.mvl.app.data.api.BooksApi
import com.mvl.app.data.api.GeocodingApi
import com.mvl.app.data.mock.MockBooksInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideMockBooksInterceptor(gson: Gson): MockBooksInterceptor =
        MockBooksInterceptor(gson)

    // OkHttpClient with mock interceptor for Books API
    @Provides
    @Singleton
    @Named("books")
    fun provideBooksOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        mockInterceptor: MockBooksInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(mockInterceptor)   // mock fires before network
        .addInterceptor(loggingInterceptor)
        .build()

    // Generic OkHttpClient for real APIs
    @Provides
    @Singleton
    @Named("real")
    fun provideRealOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideAqiApi(@Named("real") client: OkHttpClient, gson: Gson): AqiApi =
        Retrofit.Builder()
            .baseUrl("https://api.waqi.info/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AqiApi::class.java)

    @Provides
    @Singleton
    fun provideGeocodingApi(@Named("real") client: OkHttpClient, gson: Gson): GeocodingApi =
        Retrofit.Builder()
            .baseUrl("https://api.bigdatacloud.net/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GeocodingApi::class.java)

    @Provides
    @Singleton
    fun provideBooksApi(@Named("books") client: OkHttpClient, gson: Gson): BooksApi =
        Retrofit.Builder()
            .baseUrl("https://mock.mvl.app/")   // domain doesn't matter; interceptor catches it
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(BooksApi::class.java)
}
