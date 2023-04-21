package com.team.testscanner.db

import android.graphics.Bitmap
import android.graphics.PointF

data class TestPage(
    val bitmap: Bitmap,
    val coordinates: Array<Double>)
