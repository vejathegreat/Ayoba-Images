package com.velaphi.ayoba_images.data.api

import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

class CatApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: CatApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `when API call succeeds, should return cat images`() = runTest {
        // Given
        val response = listOf(
            CatImageResponse(
                id = "test_id_1",
                url = "https://test.com/image1.jpg",
                width = 800,
                height = 600
            ),
            CatImageResponse(
                id = "test_id_2",
                url = "https://test.com/image2.jpg",
                width = 800,
                height = 600
            )
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(Gson().toJson(response))
        )

        // When
        val result = apiService.getCatImages()

        // Then
        assertEquals(response.size, result.size)
        assertEquals(response[0].id, result[0].id)
        assertEquals(response[0].url, result[0].url)
        assertEquals(response[0].width, result[0].width)
        assertEquals(response[0].height, result[0].height)
    }

    @Test
    fun `when API call fails with 404, should throw exception`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
        )

        // When/Then
        try {
            apiService.getCatImages()
            assert(false) { "Expected exception was not thrown" }
        } catch (e: Exception) {
            // Expected exception
        }
    }

    @Test
    fun `when API call fails with 500, should throw exception`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Server Error")
        )

        // When/Then
        try {
            apiService.getCatImages()
            assert(false) { "Expected exception was not thrown" }
        } catch (e: Exception) {
            // Expected exception
        }
    }

    @Test
    fun `when API returns empty list, should return empty list`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
        )

        // When
        val result = apiService.getCatImages()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `when API call times out, should throw exception`() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("[]")
                .setBodyDelay(5000, TimeUnit.MILLISECONDS)
        )

        // When/Then
        try {
            apiService.getCatImages()
            assert(false) { "Expected exception was not thrown" }
        } catch (e: Exception) {
            // Expected exception
        }
    }
} 