package com.aditya.tune_together.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object UtilityFuctions {

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatOffsetDateTime(startTime: String): String {
        val offsetDateTime = OffsetDateTime.parse(startTime)
        val localDateTime = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("hh:mm a, dd MMM yyyy", Locale.getDefault())
        return localDateTime.format(formatter)
    }







}