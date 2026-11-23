package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ContentItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CreatorDeskDatabase : RoomDatabase() {
    abstract fun contentItemDao(): ContentItemDao

    companion object {
        @Volatile
        private var INSTANCE: CreatorDeskDatabase? = null

        fun getInstance(context: Context): CreatorDeskDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CreatorDeskDatabase::class.java,
                    "creator_desk.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = false)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
