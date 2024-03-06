package cz.rimu.prodlouzenevikendy.model

import java.time.LocalDate

data class HolidayRecommendation(
    val rate: Float = 0f,
    val days: List<Int> = emptyList(),
    val daysDates: List<LocalDate> = emptyList()
)