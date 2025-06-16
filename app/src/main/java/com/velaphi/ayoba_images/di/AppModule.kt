package com.velaphi.ayoba_images.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.velaphi.ayoba_images.data.db.AppDatabase
import com.velaphi.ayoba_images.data.db.CatImageDao
import com.velaphi.ayoba_images.util.ConnectivityHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideConnectivityHelper(context: Context): ConnectivityHelper {
        return ConnectivityHelper(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        Log.d("AppModule", "Creating database instance")
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ayoba_images_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideCatImageDao(database: AppDatabase): CatImageDao {
        Log.d("AppModule", "Providing CatImageDao")
        return database.catImageDao()
    }
} 