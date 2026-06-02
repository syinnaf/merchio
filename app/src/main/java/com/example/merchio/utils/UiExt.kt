package com.example.merchio.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.merchio.R

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.visible(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}

fun ImageView.loadMerchioImage(url: String?) {
    if (url.isNullOrBlank()) {
        setImageResource(R.drawable.logo_merchio)
        return
    }
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.logo_merchio)
        .error(R.drawable.logo_merchio)
        .centerCrop()
        .into(this)
}
