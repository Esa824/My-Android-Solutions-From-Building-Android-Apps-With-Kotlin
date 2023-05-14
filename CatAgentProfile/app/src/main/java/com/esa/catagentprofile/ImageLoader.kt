package com.esa.catagentprofile

import android.widget.ImageView


interface ImageLoader {
    fun loadImage(imageUrl: String, imageView: ImageView)
}