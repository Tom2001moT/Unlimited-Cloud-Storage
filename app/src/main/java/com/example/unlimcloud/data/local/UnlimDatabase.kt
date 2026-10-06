package com.example.unlimcloud.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CachedFileEntity::class], version = 1, exportSchema = false)
abstract class UnlimDatabase : RoomDatabase() {

    abstract fun cachedFileDao(): CachedFileDao

    companion object {
        @Volatile
        private var INSTANCE: UnlimDatabase? = null

        fun getInstance(context: Context): UnlimDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UnlimDatabase::class.java,
                    "unlim_cloud_local.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
