package com.velaphi.ayoba_images.data.repository

import com.velaphi.ayoba_images.data.api.CatApiService
import com.velaphi.ayoba_images.data.db.CatImageDao
import com.velaphi.ayoba_images.data.model.CatImage
import com.velaphi.ayoba_images.util.ConnectivityHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatImageRepository @Inject constructor(
    private val api: CatApiService,
    private val dao: CatImageDao,
    private val connectivityHelper: ConnectivityHelper
) {
    private val BATCH_SIZE = 100

    fun getAllCatImages(): Flow<List<CatImage>> = dao.getAllCatImages()

    suspend fun loadMoreCatImages(page: Int): List<CatImage> = withContext(Dispatchers.IO) {
        try {
            if (!connectivityHelper.isConnected()) {
                throw Exception("No internet connection available")
            }

            val response = api.getCatImages(limit = BATCH_SIZE)
            
            if (response.isEmpty()) {
                return@withContext emptyList()
            }
            
            val images = response.mapIndexed { index, catResponse ->
                CatImage(
                    remoteId = catResponse.id,
                    imageUrl = catResponse.url,
                    title = "Cat Image ${(page * BATCH_SIZE) + index + 1}",
                    description = "A beautiful cat image with dimensions ${catResponse.width}x${catResponse.height}"
                )
            }
            
            dao.insertCatImages(images)
            images
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun refreshCatImages(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!connectivityHelper.isConnected()) {
                throw Exception("No internet connection available")
            }

            dao.deleteAllCatImages()
            val images = loadMoreCatImages(0)
            
            if (images.isEmpty()) {
                throw Exception("No images available")
            }
            
            true
        } catch (e: Exception) {
            throw e
        }
    }
}