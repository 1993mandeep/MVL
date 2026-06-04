package com.mvl.app.di

import android.content.Context
import androidx.room.Room
import com.mvl.app.data.local.MVLDatabase
import com.mvl.app.data.local.dao.LocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MVLDatabase =
        Room.databaseBuilder(context, MVLDatabase::class.java, "mvl_db")
            .build()

    @Provides
    fun provideLocationDao(db: MVLDatabase): LocationDao = db.locationDao()
}
