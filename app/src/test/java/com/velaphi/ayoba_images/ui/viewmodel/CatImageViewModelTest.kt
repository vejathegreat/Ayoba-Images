package com.velaphi.ayoba_images.ui.viewmodel

import com.velaphi.ayoba_images.data.model.CatImage
import com.velaphi.ayoba_images.data.repository.CatImageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatImageViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: CatImageRepository
    private lateinit var viewModel: CatImageViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = CatImageViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when initializing viewModel, should load images`() = runTest {
        // Given
        val images = listOf(
            CatImage(
                remoteId = "test_id_1",
                imageUrl = "https://test.com/image1.jpg",
                title = "Test Cat 1",
                description = "Test Description 1"
            ),
            CatImage(
                remoteId = "test_id_2",
                imageUrl = "https://test.com/image2.jpg",
                title = "Test Cat 2",
                description = "Test Description 2"
            )
        )
        coEvery { repository.getAllCatImages() } returns flowOf(images)
        coEvery { repository.loadMoreCatImages(0) } returns images

        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is CatImageViewModel.UiState.Success)
        val successState = viewModel.uiState.value as CatImageViewModel.UiState.Success
        assertEquals(images, successState.images)
        coVerify { repository.loadMoreCatImages(0) }
    }

    @Test
    fun `when initializing viewModel and loading fails, state should be Error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { repository.getAllCatImages() } throws Exception(errorMessage)
        coEvery { repository.loadMoreCatImages(0) } throws Exception(errorMessage)

        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is CatImageViewModel.UiState.Error)
        val errorState = viewModel.uiState.value as CatImageViewModel.UiState.Error
        assertEquals(errorMessage, errorState.message)
    }

    @Test
    fun `when refreshing images succeeds, state should be Success`() = runTest {
        // Given
        val images = listOf(
            CatImage(
                remoteId = "test_id_1",
                imageUrl = "https://test.com/image1.jpg",
                title = "Test Cat 1",
                description = "Test Description 1"
            )
        )
        coEvery { repository.refreshCatImages() } returns true
        coEvery { repository.getAllCatImages() } returns flowOf(images)

        // When
        viewModel.refreshImages()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is CatImageViewModel.UiState.Success)
        val successState = viewModel.uiState.value as CatImageViewModel.UiState.Success
        assertEquals(images, successState.images)
        coVerify { repository.refreshCatImages() }
    }

    @Test
    fun `when refreshing images fails, state should be Error`() = runTest {
        // Given
        val errorMessage = "Failed to refresh images"
        coEvery { repository.refreshCatImages() } throws Exception(errorMessage)

        // When
        viewModel.refreshImages()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is CatImageViewModel.UiState.Error)
        val errorState = viewModel.uiState.value as CatImageViewModel.UiState.Error
        assertEquals(errorMessage, errorState.message)
    }

    @Test
    fun `when loading more images succeeds, should update state`() = runTest {
        // Given
        val initialImages = listOf(
            CatImage(
                remoteId = "test_id_1",
                imageUrl = "https://test.com/image1.jpg",
                title = "Test Cat 1",
                description = "Test Description 1"
            )
        )
        val moreImages = listOf(
            CatImage(
                remoteId = "test_id_2",
                imageUrl = "https://test.com/image2.jpg",
                title = "Test Cat 2",
                description = "Test Description 2"
            )
        )
        coEvery { repository.getAllCatImages() } returns flowOf(initialImages + moreImages)
        coEvery { repository.loadMoreCatImages(any()) } returns moreImages

        // When
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.loadMoreImages()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is CatImageViewModel.UiState.Success)
        val successState = viewModel.uiState.value as CatImageViewModel.UiState.Success
        assertEquals(initialImages + moreImages, successState.images)
        coVerify { repository.loadMoreCatImages(any()) }
    }

    @Test
    fun `when loading more images fails, should maintain current state`() = runTest {
        // Given
        val initialImages = listOf(
            CatImage(
                remoteId = "test_id_1",
                imageUrl = "https://test.com/image1.jpg",
                title = "Test Cat 1",
                description = "Test Description 1"
            )
        )
        coEvery { repository.getAllCatImages() } returns flowOf(initialImages)
        coEvery { repository.loadMoreCatImages(any()) } throws Exception("Failed to load more images")

        // When
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.loadMoreImages()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is CatImageViewModel.UiState.Success)
        val successState = viewModel.uiState.value as CatImageViewModel.UiState.Success
        assertEquals(initialImages, successState.images)
    }
} 