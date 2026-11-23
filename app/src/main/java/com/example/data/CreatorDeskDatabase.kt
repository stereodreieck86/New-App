package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ContentItemEntity::class],
    version = 2,
    exportSchema = false
)
abstract class CreatorDeskDatabase : RoomDatabase() {
    abstract fun contentItemDao(): ContentItemDao

    companion object {
        @Volatile
        private var INSTANCE: CreatorDeskDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE content_items ADD COLUMN publishedAtEpochDay INTEGER")
            }
        }

        fun getInstance(context: Context): CreatorDeskDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CreatorDeskDatabase::class.java,
                    "creator_desk.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration(dropAllTables = false)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
