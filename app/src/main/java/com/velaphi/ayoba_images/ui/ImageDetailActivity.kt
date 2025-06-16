package com.velaphi.ayoba_images.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.velaphi.ayoba_images.R
import com.velaphi.ayoba_images.data.model.CatImage
import com.velaphi.ayoba_images.databinding.ActivityImageDetailBinding

class ImageDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val catImage = intent.getParcelableExtra<CatImage>(EXTRA_CAT_IMAGE)
        if (catImage != null) {
            setupUI(catImage)
        } else {
            finish()
        }
    }

    private fun setupUI(catImage: CatImage) {
        binding.descriptionTextView.text = catImage.description

        Glide.with(this)
            .load(catImage.imageUrl)
            .placeholder(R.drawable.angry_cat)
            .error(R.drawable.alert_error_)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerInside()
            .into(binding.imageView)
    }

    companion object {
        const val EXTRA_CAT_IMAGE = "extra_cat_image"
    }
}