package com.mvl.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mvl.app.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(location: LocationEntity)

    @Query("SELECT * FROM cached_locations WHERE coordKey = :coordKey LIMIT 1")
    suspend fun getByCoordKey(coordKey: String): LocationEntity?

    /** All cached locations ordered by most recently cached, for Screen 5 picker */
    @Query("SELECT * FROM cached_locations ORDER BY cachedAt DESC")
    fun getAllFlow(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM cached_locations ORDER BY cachedAt DESC")
    suspend fun getAll(): List<LocationEntity>

    @Query("UPDATE cached_locations SET nickname = :nickname WHERE coordKey = :coordKey")
    suspend fun updateNickname(coordKey: String, nickname: String)

}
