package com.velaphi.ayoba_images.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "cat_images")
@Parcelize
data class CatImage(
    @PrimaryKey
    val remoteId: String,
    val imageUrl: String,
    val title: String,
    val description: String
) : Parcelable