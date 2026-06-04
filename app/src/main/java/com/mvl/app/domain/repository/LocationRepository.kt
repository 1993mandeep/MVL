package com.mvl.app.domain.repository

import com.mvl.app.domain.model.BookingRecord
import com.mvl.app.domain.model.LocationPoint
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun fetchAqi(lat: Double, lng: Double): Int
    suspend fun fetchLocationPoint(lat: Double, lng: Double): LocationPoint
    suspend fun saveNickname(lat: Double, lng: Double, nickname: String)
    suspend fun createBooking(a: LocationPoint, b: LocationPoint): BookingRecord
    suspend fun getBookings(year: Int, month: Int): List<BookingRecord>
    fun cachedLocationsFlow(): Flow<List<LocationPoint>>
}