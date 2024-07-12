package cz.rimu.prodlouzenevikendy.model

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import java.util.Date

@Immutable
data class ExtendedPublicHoliday(
    val localName: String,
    val name: String,
    val date: Date?,
    val recommendedDays: List<Recommendation>,
    val isVisible: Boolean
)

@Immutable
data class Recommendation(
    val days: List<LocalDate>,
    val isSelected: Boolean,
    val isVisible: Boolean
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