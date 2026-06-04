package com.mvl.app.data.api

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query

interface AqiApi {
    @GET("feed/geo:{lat};{lng}/")
    suspend fun getAqi(
        @retrofit2.http.Path("lat") lat: Double,
        @retrofit2.http.Path("lng") lng: Double,
        @Query("token") token: String
    ): AqiResponse
}

interface GeocodingApi {
    @GET("data/reverse-geocode-client")
    suspend fun reverseGeocode(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("localityLanguage") language: String = "en"
    ): GeocodingResponse
}

interface BooksApi {
    @POST("books")
    suspend fun createBooking(@Body request: BookRequest): BookResponse

    @GET("books")
    suspend fun getBookings(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): List<BookResponse>
}
