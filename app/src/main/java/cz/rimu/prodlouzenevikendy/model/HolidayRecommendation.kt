package cz.rimu.prodlouzenevikendy.model

data class HolidayRecommendation(
    val rate: Float = 0f,
    val days: List<Int> = emptyList()
)