package com.velaphi.ayoba_images.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.velaphi.ayoba_images.data.model.CatImage
import com.velaphi.ayoba_images.data.repository.CatImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatImageViewModel @Inject constructor(
    private val repository: CatImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var currentPage = 0
    private var isLoading = false
    private var hasMoreImages = true
    private var isInitialLoad = true

    init {
        if (isInitialLoad) {
            loadCatImages()
            isInitialLoad = false
        }
    }

    fun refreshImages() {
        viewModelScope.launch {
            currentPage = 0
            hasMoreImages = true
            _uiState.value = UiState.Loading
            try {
                repository.refreshCatImages()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to refresh images")
            }
        }
    }

    fun loadMoreImages() {
        if (isLoading || !hasMoreImages) return
        
        viewModelScope.launch {
            isLoading = true
            try {
                val newImages = repository.loadMoreCatImages(currentPage)
                if (newImages.isEmpty()) {
                    hasMoreImages = false
                } else {
                    currentPage++
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load more images")
            } finally {
                isLoading = false
            }
        }
    }

    private fun loadCatImages() {
        viewModelScope.launch {
            try {
                repository.getAllCatImages().collect { images ->
                    _uiState.update { currentState ->
                        when {
                            images.isEmpty() -> UiState.Empty
                            else -> UiState.Success(images)
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    sealed class UiState {
        data object Loading : UiState()
        data object Empty : UiState()
        data class Success(val images: List<CatImage>) : UiState()
        data class Error(val message: String) : UiState()
    }
}