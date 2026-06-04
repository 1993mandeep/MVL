package com.mvl.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mvl.app.data.local.dao.LocationDao
import com.mvl.app.data.local.entity.LocationEntity

@Database(
    entities = [LocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MVLDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}
