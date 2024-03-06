package cz.rimu.prodlouzenevikendy.model

import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import java.util.Date

data class ExtendedPublicHoliday(
    val localName: String,
    val name: String,
    var date: Date?,
    var recommendedDays: List<List<LocalDate>>
)

// from Date to YearMonth
fun Date.toYearMonth(): YearMonth {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
}

// from Date to LocalDate
fun Date.toLocalDate(): LocalDate {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return LocalDate.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}

