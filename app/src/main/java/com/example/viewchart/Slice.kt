package com.example.viewchart

data class Slice(
    var percentage: Float,
    var color: Int,
    var startAngle: Float = 0f,
    var sweepSize: Float = 0f,
)