package cz.rimu.prodlouzenevikendy.model

data class HolidayRecommendation(
    var rate: Float = 0f,
    var days: List<Int> = emptyList()
)