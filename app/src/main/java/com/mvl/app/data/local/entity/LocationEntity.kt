package com.mvl.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cached location entry.
 * Primary key is a rounded coordinate key "lat3,lng3" (3 decimal places).
 * Two coordinates are considered the same if they match to 3 decimal places per spec.
 */
@Entity(tableName = "cached_locations")
data class LocationEntity(
    @PrimaryKey
    val coordKey: String,       // "{lat_3dp},{lng_3dp}" e.g. "37.564,127.001"
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val aqi: Int,
    val nickname: String = "",
    val cachedAt: Long = System.currentTimeMillis()
)
