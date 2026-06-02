package com.example.merchio.utils

import android.widget.EditText

fun EditText.textString(): String = text?.toString()?.trim().orEmpty()
fun String?.orDash(): String = if (this.isNullOrBlank()) "-" else this
