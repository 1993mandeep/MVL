package com.mvl.app.data.api

import com.google.gson.annotations.SerializedName

// ---- AQI (aqicn.org) ----
data class AqiResponse(
    val status: String,
    val data: AqiData?
)

data class AqiData(
    val aqi: Int
)

// ---- Reverse Geocoding (bigdatacloud.com) ----
data class GeocodingResponse(
    val localityInfo: LocalityInfo?
)

data class LocalityInfo(
    val administrative: List<AdminEntry>?
)

data class AdminEntry(
    val order: Int,
    val name: String
)

// ---- Books API (mocked) ----
data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val aqi: Int,
    val name: String
)

data class BookRequest(
    val a: LocationDto,
    val b: LocationDto
)

data class BookResponse(
    val id: String?,
    val a: LocationDto,
    val b: LocationDto,
    val price: Double
)
