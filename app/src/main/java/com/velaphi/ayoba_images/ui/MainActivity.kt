package com.velaphi.ayoba_images.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.velaphi.ayoba_images.databinding.ActivityMainBinding
import com.velaphi.ayoba_images.ui.adapter.CatImageAdapter
import com.velaphi.ayoba_images.ui.viewmodel.CatImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: CatImageViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: CatImageAdapter
    private var currentState: CatImageViewModel.UiState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSwipeRefresh()
        setupErrorHandling()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = CatImageAdapter { catImage ->
            val intent = Intent(this, ImageDetailActivity::class.java).apply {
                putExtra(ImageDetailActivity.EXTRA_CAT_IMAGE, catImage)
            }
            startActivity(intent)
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = this@MainActivity.adapter
            setHasFixedSize(true)
            addOnScrollListener(createScrollListener())
        }
    }

    private fun createScrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as GridLayoutManager
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            val totalItemCount = layoutManager.itemCount

            if (lastVisibleItem >= totalItemCount - 10) {
                viewModel.loadMoreImages()
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshImages()
        }
    }

    private fun setupErrorHandling() {
        binding.retryButton.setOnClickListener {
            viewModel.refreshImages()
        }
        binding.refreshButton.setOnClickListener {
            viewModel.refreshImages()
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.onEach { state ->
            currentState = state
            updateUI(state)
        }.launchIn(lifecycleScope)
    }

    private fun updateUI(state: CatImageViewModel.UiState) {
        when (state) {
            is CatImageViewModel.UiState.Loading -> {
                if (!binding.swipeRefreshLayout.isRefreshing) {
                    binding.progressBar.visibility = View.VISIBLE
                }
                binding.errorView.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.GONE
            }
            is CatImageViewModel.UiState.Success -> {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.progressBar.visibility = View.GONE
                binding.errorView.visibility = View.GONE
                binding.emptyView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(state.images)
            }
            is CatImageViewModel.UiState.Empty -> {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.progressBar.visibility = View.GONE
                binding.errorView.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            }
            is CatImageViewModel.UiState.Error -> {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.GONE
                binding.errorView.visibility = View.VISIBLE
                binding.errorText.text = state.message
            }
        }
    }
}