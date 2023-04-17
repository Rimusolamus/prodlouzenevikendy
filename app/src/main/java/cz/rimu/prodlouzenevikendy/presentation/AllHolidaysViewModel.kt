package cz.rimu.prodlouzenevikendy.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.rimu.prodlouzenevikendy.domain.PublicHolidaysRepository
import cz.rimu.prodlouzenevikendy.model.PublicHoliday
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Year
import java.time.YearMonth
import java.util.*

class AllHolidaysViewModel(
    private val publicHolidaysRepository: PublicHolidaysRepository
) : ViewModel() {

    private val _publicHolidays = MutableStateFlow<List<PublicHoliday>>(emptyList())
    val publicHolidays: StateFlow<List<PublicHoliday>> = _publicHolidays

    init {
        viewModelScope.launch {
            _publicHolidays.value = publicHolidaysRepository.getPublicHolidays()
        }
    }

    // 0 - working day
    // 1 - public holiday
    // 2 - weekend
    private fun makeListOfWorkingDaysOfTheYear(publicHolidays: List<PublicHoliday>): List<Int> {
        val workingDaysOfTheYear = mutableListOf<Int>()
        val wholeYear = List(getNumberOfDaysInCurrentYear()) { 0 }
        return wholeYear
    }

    private fun getNumberOfDaysInCurrentYear(): Int {
        // Get the current year
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        // Create a Calendar object representing the current year and the first month (January)
        val calendar = Calendar.getInstance()
        calendar.set(currentYear, Calendar.JANUARY, 1)

        // Set the time to the last day of the year
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR))

        // Get the day of the year, which gives the number of days in the current year
        return calendar.get(Calendar.DAY_OF_YEAR)
    }

    private fun parseDateString(dateString: String): Date? {
        // Define the format pattern
        val pattern = "yyyy-MM-dd"

        return try {
            // Create a DateTimeFormatter with the specified pattern
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())

            // Parse the date string using the formatter
            formatter.parse(dateString)
        } catch (e: Exception) {
            // Handle any exceptions that occur during parsing
            println("Failed to parse date string: $dateString")
            null
        }
    }
}