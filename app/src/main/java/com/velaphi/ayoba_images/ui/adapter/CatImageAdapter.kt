package com.velaphi.ayoba_images.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.velaphi.ayoba_images.R
import com.velaphi.ayoba_images.data.model.CatImage

class CatImageAdapter(
    private val onItemClick: (CatImage) -> Unit
) : ListAdapter<CatImage, CatImageAdapter.CatImageViewHolder>(CatImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cat_image, parent, false)
        return CatImageViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: CatImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CatImageViewHolder(
        itemView: View,
        private val onItemClick: (CatImage) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.catImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

        fun bind(catImage: CatImage) {
            titleTextView.text = catImage.title
            descriptionTextView.text = catImage.description

            Glide.with(itemView.context)
                .load(catImage.imageUrl)
                .placeholder(R.drawable.angry_cat)
                .error(R.drawable.alert_error_)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(imageView)

            itemView.setOnClickListener {
                onItemClick(catImage)
            }
        }
    }

    private class CatImageDiffCallback : DiffUtil.ItemCallback<CatImage>() {
        override fun areItemsTheSame(oldItem: CatImage, newItem: CatImage): Boolean {
            return oldItem.remoteId == newItem.remoteId
        }

        override fun areContentsTheSame(oldItem: CatImage, newItem: CatImage): Boolean {
            return oldItem == newItem
        }
    }
}