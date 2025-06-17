package com.velaphi.ayoba_images.data.repository

import com.velaphi.ayoba_images.data.api.CatApiService
import com.velaphi.ayoba_images.data.api.CatImageResponse
import com.velaphi.ayoba_images.data.db.CatImageDao
import com.velaphi.ayoba_images.data.model.CatImage
import com.velaphi.ayoba_images.util.ConnectivityHelper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatImageRepositoryTest {

    private lateinit var apiService: CatApiService
    private lateinit var catImageDao: CatImageDao
    private lateinit var repository: CatImageRepository
    private lateinit var connectivityHelper: ConnectivityHelper

    @Before
    fun setup() {
        apiService = mockk()
        catImageDao = mockk()
        connectivityHelper = mockk()
        repository = CatImageRepository(apiService, catImageDao, connectivityHelper)
    }

    @Test
    fun `when getAllCatImages is called, should return images from dao`() = runTest {
        // Given
        val images = listOf(
            CatImage(
                remoteId = "test_id_1",
                imageUrl = "https://test.com/image1.jpg",
                title = "Test Cat 1",
                description = "Test Description 1"
            )
        )
        coEvery { catImageDao.getAllCatImages() } returns flowOf(images)

        // When
        val result = repository.getAllCatImages().first()

        // Then
        assertEquals(images, result)
    }

    @Test
    fun `when loadMoreCatImages is called, should fetch from api and save to dao`() = runTest {
        // Given
        val apiResponse = listOf(
            CatImageResponse(
                id = "test_id_1",
                url = "https://test.com/image1.jpg",
                width = 800,
                height = 600
            )
        )
        val expectedImages = listOf(
            CatImage(
                remoteId = "test_id_1",
                imageUrl = "https://test.com/image1.jpg",
                title = "Unknown",
                description = "No description available"
            )
        )
        coEvery { apiService.getCatImages(any(), any()) } returns apiResponse
        coEvery { catImageDao.insertCatImages(any()) } returns Unit
        coEvery { connectivityHelper.isConnected() } returns true

        // When
        val result = repository.loadMoreCatImages(0)

        // Then
        assertEquals(expectedImages, result)
        coVerify { catImageDao.insertCatImages(expectedImages) }
    }

    @Test
    fun `when refreshCatImages is called, should clear dao and fetch new images`() = runTest {
        // Given
        val apiResponse = listOf(
            CatImageResponse(
                id = "test_id_1",
                url = "https://test.com/image1.jpg",
                width = 800,
                height = 600
            )
        )
        val expectedImages = listOf(
            CatImage(
                remoteId = "test_id_1",
                imageUrl = "https://test.com/image1.jpg",
                title = "Unknown",
                description = "No description available"
            )
        )
        coEvery { apiService.getCatImages(any(), any()) } returns apiResponse
        coEvery { catImageDao.deleteAllCatImages() } returns Unit
        coEvery { catImageDao.insertCatImages(any()) } returns Unit
        coEvery { connectivityHelper.isConnected() } returns true

        // When
        val result = repository.refreshCatImages()

        // Then
        assertTrue(result)
        coVerify { catImageDao.deleteAllCatImages() }
        coVerify { catImageDao.insertCatImages(expectedImages) }
    }

    @Test
    fun `when loadMoreCatImages fails, should throw exception`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { apiService.getCatImages(any(), any()) } throws Exception(errorMessage)
        coEvery { connectivityHelper.isConnected() } returns true

        // When/Then
        try {
            repository.loadMoreCatImages(0)
            assertTrue(false) // Should not reach here
        } catch (e: Exception) {
            assertEquals(errorMessage, e.message)
        }
    }

    @Test
    fun `when refreshCatImages fails, should throw exception`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { apiService.getCatImages(any(), any()) } throws Exception(errorMessage)
        coEvery { connectivityHelper.isConnected() } returns true

        // When/Then
        try {
            repository.refreshCatImages()
            assertTrue(false) // Should not reach here
        } catch (e: Exception) {
            assertEquals(errorMessage, e.message)
        }
    }

    @Test
    fun `when network is not available, should throw exception`() = runTest {
        // Given
        coEvery { connectivityHelper.isConnected() } returns false

        // When/Then
        try {
            repository.loadMoreCatImages(0)
            assertTrue(false) // Should not reach here
        } catch (e: Exception) {
            assertEquals("No internet connection", e.message)
        }
    }
} 