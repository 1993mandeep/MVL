package com.mvl.app.data.repository

import com.mvl.app.BuildConfig
import com.mvl.app.data.api.AqiApi
import com.mvl.app.data.api.BookRequest
import com.mvl.app.data.api.BooksApi
import com.mvl.app.data.api.GeocodingApi
import com.mvl.app.data.api.LocationDto
import com.mvl.app.data.local.dao.LocationDao
import com.mvl.app.data.local.entity.LocationEntity
import com.mvl.app.domain.model.BookingRecord
import com.mvl.app.domain.model.LocationPoint
import com.mvl.app.domain.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val aqiApi: AqiApi,
    private val geocodingApi: GeocodingApi,
    private val booksApi: BooksApi,
    private val locationDao: LocationDao
) : LocationRepository {

    override suspend fun fetchAqi(lat: Double, lng: Double): Int = withContext(Dispatchers.IO) {
        runCatching {
            aqiApi.getAqi(
                lat = roundCoord(lat),
                lng = roundCoord(lng),
                token = BuildConfig.AQI_API_KEY
            ).data?.aqi ?: -1
        }.getOrDefault(-1)
    }

    override suspend fun fetchLocationPoint(lat: Double, lng: Double): LocationPoint =
        withContext(Dispatchers.IO) {
            val key = coordKey(lat, lng)
            val cached = locationDao.getByCoordKey(key)
            if (cached != null) return@withContext cached.toDomain()
            val name = fetchAddressName(lat, lng)
            val aqi = fetchAqi(lat, lng)
            LocationPoint(lat, lng, name, aqi).also { locationDao.upsert(it.toEntity(key)) }
        }

    override suspend fun saveNickname(lat: Double, lng: Double, nickname: String) =
        withContext(Dispatchers.IO) {
            locationDao.updateNickname(coordKey(lat, lng), nickname)
        }

    override suspend fun createBooking(a: LocationPoint, b: LocationPoint): BookingRecord =
        withContext(Dispatchers.IO) {
            booksApi.createBooking(BookRequest(a.toDto(), b.toDto())).toDomain()
        }

    override suspend fun getBookings(year: Int, month: Int): List<BookingRecord> =
        withContext(Dispatchers.IO) {
            booksApi.getBookings(year, month).map { it.toDomain() }
        }

    override fun cachedLocationsFlow(): Flow<List<LocationPoint>> =
        locationDao.getAllFlow().map { list -> list.map { it.toDomain() } }

    // ---- Private helpers ----

    private suspend fun fetchAddressName(lat: Double, lng: Double): String =
        runCatching {
            geocodingApi.reverseGeocode(latitude = lat, longitude = lng)
                .localityInfo?.administrative
                ?.sortedByDescending { it.order }
                ?.take(2)
                ?.joinToString(", ") { it.name }
                ?: "Unknown"
        }.getOrDefault("Unknown")

    private fun roundCoord(value: Double) = (value * 1000).roundToInt() / 1000.0
    private fun coordKey(lat: Double, lng: Double) = "${roundCoord(lat)},${roundCoord(lng)}"

    private fun LocationEntity.toDomain() = LocationPoint(latitude, longitude, name, aqi, nickname)
    private fun LocationPoint.toEntity(key: String) =
        LocationEntity(key, latitude, longitude, name, aqi, nickname)
    private fun LocationPoint.toDto() = LocationDto(latitude, longitude, aqi, name)
    private fun com.mvl.app.data.api.BookResponse.toDomain() = BookingRecord(
        id = id,
        a = LocationPoint(a.latitude, a.longitude, a.name, a.aqi),
        b = LocationPoint(b.latitude, b.longitude, b.name, b.aqi),
        price = price
    )
}
