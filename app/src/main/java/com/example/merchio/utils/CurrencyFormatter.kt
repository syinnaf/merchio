package com.example.merchio.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        maximumFractionDigits = 0
    }

    fun rupiah(value: Double): String = formatter.format(value).replace("Rp", "Rp")
}
