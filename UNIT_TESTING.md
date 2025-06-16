# Unit Testing Guide with MockK

## Table of Contents
1. [Setup](#setup)
2. [ViewModel Tests](#viewmodel-tests)
3. [Repository Tests](#repository-tests)
4. [API Service Tests](#api-service-tests)
5. [Database Tests](#database-tests)
6. [Test Utilities](#test-utilities)

## Setup

### Dependencies
```gradle
dependencies {
    // Testing
    testImplementation "io.mockk:mockk:1.13.8"
    testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3"
    testImplementation "androidx.arch.core:core-testing:2.2.0"
    testImplementation "junit:junit:4.13.2"
    testImplementation "org.jetbrains.kotlin:kotlin-test-junit:1.9.0"
}
```

### Test Rules
```kotlin
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
```

## ViewModel Tests

### CatImageViewModel Tests
```kotlin
class CatImageViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: CatImageRepository
    private lateinit var viewModel: CatImageViewModel

    @Before
    fun setup() {
        repository = mockk()
        viewModel = CatImageViewModel(repository)
    }

    @Test
    fun `when loading images succeeds, state should be Success`() = runTest {
        // Given
        val images = listOf(createTestCatImage())
        coEvery { repository.getAllCatImages() } returns flowOf(images)

        // When
        viewModel.loadCatImages()

        // Then
        assert(viewModel.uiState.value is CatImageViewModel.UiState.Success)
        val successState = viewModel.uiState.value as CatImageViewModel.UiState.Success
        assertEquals(images, successState.images)
    }

    @Test
    fun `when loading images fails, state should be Error`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { repository.getAllCatImages() } throws Exception(errorMessage)

        // When
        viewModel.loadCatImages()

        // Then
        assert(viewModel.uiState.value is CatImageViewModel.UiState.Error)
        val errorState = viewModel.uiState.value as CatImageViewModel.UiState.Error
        assertEquals(errorMessage, errorState.message)
    }

    @Test
    fun `when refreshing images succeeds, state should be Success`() = runTest {
        // Given
        val images = listOf(createTestCatImage())
        coEvery { repository.refreshCatImages() } returns true
        coEvery { repository.getAllCatImages() } returns flowOf(images)

        // When
        viewModel.refreshImages()

        // Then
        assert(viewModel.uiState.value is CatImageViewModel.UiState.Success)
        val successState = viewModel.uiState.value as CatImageViewModel.UiState.Success
        assertEquals(images, successState.images)
    }
}
```

## Repository Tests

### CatImageRepository Tests
```kotlin
class CatImageRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var api: CatApiService
    private lateinit var dao: CatImageDao
    private lateinit var connectivityHelper: ConnectivityHelper
    private lateinit var repository: CatImageRepository

    @Before
    fun setup() {
        api = mockk()
        dao = mockk()
        connectivityHelper = mockk()
        repository = CatImageRepository(api, dao, connectivityHelper)
    }

    @Test
    fun `when loading more images succeeds, should return images`() = runTest {
        // Given
        val apiResponse = listOf(createTestCatImageResponse())
        val expectedImages = apiResponse.map { it.toEntity(0, 0) }
        every { connectivityHelper.isConnected() } returns true
        coEvery { api.getCatImages(any()) } returns apiResponse
        coEvery { dao.insertCatImages(any()) } just Runs

        // When
        val result = repository.loadMoreCatImages(0)

        // Then
        assertEquals(expectedImages.size, result.size)
        coVerify { dao.insertCatImages(any()) }
    }

    @Test
    fun `when no internet connection, should throw exception`() = runTest {
        // Given
        every { connectivityHelper.isConnected() } returns false

        // When/Then
        assertThrows<Exception> {
            repository.loadMoreCatImages(0)
        }
    }
}
```

## API Service Tests

### CatApiService Tests
```kotlin
class CatApiServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: CatApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
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
        val response = listOf(createTestCatImageResponse())
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
    }
}
```

## Database Tests

### CatImageDao Tests
```kotlin
@RunWith(AndroidJUnit4::class)
class CatImageDaoTest {
    private lateinit var database: CatImageDatabase
    private lateinit var dao: CatImageDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, CatImageDatabase::class.java
        ).build()
        dao = database.catImageDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun `when inserting images, should be able to retrieve them`() = runBlocking {
        // Given
        val images = listOf(createTestCatImage())

        // When
        dao.insertCatImages(images)
        val result = dao.getAllCatImages().first()

        // Then
        assertEquals(images.size, result.size)
        assertEquals(images[0].remoteId, result[0].remoteId)
    }
}
```

## Test Utilities

### Test Data Factory
```kotlin
object TestDataFactory {
    fun createTestCatImage() = CatImage(
        remoteId = "test_id",
        imageUrl = "https://test.com/image.jpg",
        title = "Test Cat",
        description = "Test Description"
    )

    fun createTestCatImageResponse() = CatImageResponse(
        id = "test_id",
        url = "https://test.com/image.jpg",
        width = 800,
        height = 600
    )

    fun createTestCatImages(count: Int): List<CatImage> {
        return (0 until count).map { index ->
            CatImage(
                remoteId = "test_id_$index",
                imageUrl = "https://test.com/image_$index.jpg",
                title = "Test Cat $index",
                description = "Test Description $index"
            )
        }
    }
}
```

### Test Extensions
```kotlin
fun <T> Flow<T>.test(
    timeoutMs: Long = 1000L,
    validate: suspend FlowCollector<T>.() -> Unit
) = runTest {
    withTimeout(timeoutMs) {
        collect { validate() }
    }
}

suspend fun <T> Flow<T>.first(): T {
    var value: T? = null
    collect { value = it }
    return value ?: throw NoSuchElementException("Flow is empty")
}
```

## Best Practices

1. **Test Structure**
   - Use Given/When/Then pattern
   - One assertion per test
   - Clear test names
   - Proper setup and teardown

2. **Mocking**
   - Mock external dependencies
   - Use relaxed mocks when appropriate
   - Verify important interactions
   - Clear mocks between tests

3. **Coroutines**
   - Use TestDispatcher
   - Handle timeouts
   - Test cancellation
   - Test error cases

4. **Database**
   - Use in-memory database
   - Clean up after tests
   - Test migrations
   - Test constraints

5. **Network**
   - Use MockWebServer
   - Test error responses
   - Test timeouts
   - Test retry logic 