package com.mvl.app.domain.model

data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val name: String,        // address name from reverse geocoding
    val aqi: Int,
    val nickname: String = ""
)

data class BookingRecord(
    val id: String? = null,
    val a: LocationPoint,
    val b: LocationPoint,
    val price: Double
)

enum class SetStep { SET_A, SET_B, BOOK }
