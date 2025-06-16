package com.velaphi.ayoba_images.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.velaphi.ayoba_images.data.model.CatImage

@Database(
    entities = [CatImage::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catImageDao(): CatImageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ayoba_images_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                Log.d("AppDatabase", "Database instance created")
                instance
            }
        }
    }
} 