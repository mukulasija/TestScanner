package com.team.testscanner.models

import android.graphics.Bitmap
import android.graphics.PointF

data class TestPage(
    val bitmap: Bitmap,
    val coordinates: Array<Double>)
