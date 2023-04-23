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
            makeListOfWorkingDaysOfTheYear(_publicHolidays.value)
        }
    }

    // 0 - working day
    // 1 - weekends
    // 2 - public holidays
    private fun makeListOfWorkingDaysOfTheYear(publicHolidays: List<PublicHoliday>): List<Int> {
        val wholeYear = MutableList(getNumberOfDaysInCurrentYear()) { 0 }
        getWeekendDaysIndexes().map { wholeYear[it - 1] = 1 }
        // if public holiday is on weekend, it is not counted as public holiday here
        getPublicHolidaysIndexes(publicHolidays).map {
            if (wholeYear[it - 1] != 1) wholeYear[it - 1] = 2
        }
        return wholeYear
    }

    private fun getWeekendDaysIndexes(): List<Int> {
        val calendar = Calendar.getInstance()
        val weekendDaysIndexes = mutableListOf<Int>()

        for (day in 1..getNumberOfDaysInCurrentYear()) {
            calendar.set(Calendar.DAY_OF_YEAR, day)
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                weekendDaysIndexes.add(day)
            }
        }
        return weekendDaysIndexes
    }

    private fun getPublicHolidaysIndexes(publicHolidays: List<PublicHoliday>): List<Int> {
        val publicHolidaysIndexes = mutableListOf<Int>()
        publicHolidays.map { publicHolidaysIndexes.add(getDayOfYear(it.date)) }
        return publicHolidaysIndexes
    }

    private fun getDayOfYear(dateString: String): Int {
        val date = parseDateString(dateString)
        val calendar = Calendar.getInstance()
        return if (date != null) {
            calendar.time = date
            calendar.get(Calendar.DAY_OF_YEAR)
        } else {
            0
        }
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