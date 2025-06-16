package com.velaphi.ayoba_images.data.db

import androidx.room.*
import com.velaphi.ayoba_images.data.model.CatImage
import kotlinx.coroutines.flow.Flow

@Dao
interface CatImageDao {
    @Query("SELECT * FROM cat_images")
    fun getAllCatImages(): Flow<List<CatImage>>

    @Query("SELECT COUNT(*) FROM cat_images")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatImages(images: List<CatImage>)

    @Query("DELETE FROM cat_images")
    suspend fun deleteAllCatImages()
}