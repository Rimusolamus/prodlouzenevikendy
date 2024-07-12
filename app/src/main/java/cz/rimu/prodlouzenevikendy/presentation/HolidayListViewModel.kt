package cz.rimu.prodlouzenevikendy.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import cz.rimu.prodlouzenevikendy.domain.LocalHolidayCountRepository
import cz.rimu.prodlouzenevikendy.domain.LocalSelectedRecommendations
import cz.rimu.prodlouzenevikendy.domain.RemotePublicHolidaysRepository
import cz.rimu.prodlouzenevikendy.model.ExtendedPublicHoliday
import cz.rimu.prodlouzenevikendy.model.HolidayRecommendation
import cz.rimu.prodlouzenevikendy.model.PublicHoliday
import cz.rimu.prodlouzenevikendy.model.Recommendation
import cz.rimu.prodlouzenevikendy.model.toLocalDate
import cz.rimu.tools.presentation.AbstractViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

class HolidayListViewModel(
    private val publicHolidaysRepository: RemotePublicHolidaysRepository,
    private val selectedRecommendations: LocalSelectedRecommendations,
    holidayCountRepository: LocalHolidayCountRepository
) : AbstractViewModel<HolidayListViewModel.State>(State()) {

    private val _publicHolidays = MutableStateFlow<List<PublicHoliday>>(emptyList())

    init {
        state = state.copy(
            isLoading = true
        )

        viewModelScope.launch {
            holidayCountRepository.holidayCount.collect {
                state = state.copy(
                    vacationDaysLeft = it
                )
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            makeHolidayList()
        }
    }


    fun toggleHolidayVisibility(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            state =
                state.copy(extendedPublicHolidays = state.extendedPublicHolidays.mapIndexed { i, extendedPublicHoliday ->
                    if (i == index) {
                        extendedPublicHoliday.copy(isVisible = !extendedPublicHoliday.isVisible)
                    } else {
                        extendedPublicHoliday
                    }
                })
        }
    }

    fun toggleCalendarSelection(publicHolidayIndex: Int, calendarIndex: Int, isAdding: Boolean) {
        val extendedPublicHoliday = state.extendedPublicHolidays[publicHolidayIndex]
        val recommendedDay = extendedPublicHoliday.recommendedDays[calendarIndex]
        calculateNewVacationDaysLeft(
            recommendation = recommendedDay,
            isAdding = isAdding
        )
        selectCalendar(
            publicHolidayIndex = publicHolidayIndex,
            calendarIndex = calendarIndex,
            isAdding = isAdding
        )
    }

    private suspend fun makeHolidayList() {
        val holidays = publicHolidaysRepository.getPublicHolidays()
        _publicHolidays.value = holidays

        holidays.map { publicHoliday ->
            val extendedPublicHoliday = ExtendedPublicHoliday(
                name = publicHoliday.name,
                localName = publicHoliday.localName,
                date = parseDateString(publicHoliday.date),
                recommendedDays = listOf(),
                isVisible = false
            )
            state =
                state.copy(extendedPublicHolidays = state.extendedPublicHolidays + extendedPublicHoliday)
        }

        val wholeYear = makeListOfWorkingDaysOfTheYear(_publicHolidays.value)
        var holidayNumber = -1

        for (i in wholeYear.indices) {
            if (wholeYear[i] == 2) {
                holidayNumber++
                val holidaysIndexes = getHolidaysIndexesCloseToDate(wholeYear, i)
                val recommendToRight =
                    getRecommended(
                        wholeYear,
                        holidaysIndexes,
                        HolidayFinderDirection.RIGHT
                    )
                val recommendToLeft =
                    getRecommended(
                        wholeYear,
                        holidaysIndexes,
                        HolidayFinderDirection.LEFT
                    )
                state =
                    state.copy(extendedPublicHolidays = state.extendedPublicHolidays.mapIndexed { index, extendedPublicHoliday ->
                        if (index == holidayNumber) {
                            extendedPublicHoliday.copy(
                                recommendedDays = (recommendToRight + recommendToLeft).sortedBy { it.size }
                                    .toRecommendationList()
                            )
                        } else {
                            extendedPublicHoliday
                        }
                    })
            }
        }
        state = state.copy(isLoading = false)
    }

    private fun calculateNewVacationDaysLeft(
        recommendation: Recommendation,
        isAdding: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            recommendation.days.forEach { day ->
                if (day.dayOfWeek != DayOfWeek.SATURDAY && day.dayOfWeek != DayOfWeek.SUNDAY && state.extendedPublicHolidays.find { it.date?.toLocalDate() == day } == null) {
                    state =
                        state.copy(
                            vacationDaysLeft = if (isAdding) state.vacationDaysLeft - 1 else state.vacationDaysLeft + 1,
                        )
                }
            }
        }
    }

    private fun selectCalendar(
        publicHolidayIndex: Int,
        calendarIndex: Int,
        isAdding: Boolean
    ) {
        state = state.copy(
            extendedPublicHolidays = state.extendedPublicHolidays.mapIndexed { index, extendedPublicHoliday ->
                if (index == publicHolidayIndex) {
                    extendedPublicHoliday.copy(
                        recommendedDays = extendedPublicHoliday.recommendedDays.mapIndexed { i, recommendation ->
                            if (i == calendarIndex) {
                                if (isAdding) {
                                    viewModelScope.launch(Dispatchers.IO) {
                                        selectedRecommendations.addSelectedRecommendation(
                                            recommendation
                                        )
                                    }
                                } else {
                                    viewModelScope.launch(Dispatchers.IO) {
                                        selectedRecommendations.removeSelectedRecommendation(
                                            recommendation
                                        )
                                    }
                                }
                                recommendation.copy(isSelected = isAdding, isVisible = true)
                            } else {
                                recommendation.copy(isSelected = false, isVisible = !isAdding)
                            }
                        }
                    )
                } else {
                    extendedPublicHoliday
                }
            }
        )
    }

    private fun getRecommended(
        wholeYear: List<Int>,
        holidayIndexes: List<Int>,
        direction: HolidayFinderDirection
    ): List<List<LocalDate>> {
        var holidayRecommendation = HolidayRecommendation()
        val holidayRecommendationList = mutableListOf<HolidayRecommendation>()
        var index = holidayIndexes.last()
        var numberOfDays = 0

        val wholeYearMutable = wholeYear.toMutableList()
        while (true) {
            if (direction == HolidayFinderDirection.RIGHT) {
                index++
            } else {
                if (index == 0) {
                    return holidayRecommendationList.map { it.daysDates }
                }
                index--
            }
            if (wholeYearMutable.getOrNull(index) == 0 && wholeYearMutable.getOrNull(index) != null) {
                numberOfDays++
                wholeYearMutable[index] = 3
            }
            if (numberOfDays >= 1) {
                val updatedHolidayIndexes = getHolidaysIndexesCloseToDate(wholeYearMutable, index)

                index = if (direction == HolidayFinderDirection.RIGHT) {
                    updatedHolidayIndexes.last()
                } else {
                    updatedHolidayIndexes.first()
                }
                holidayRecommendation = holidayRecommendation.copy(
                    rate = updatedHolidayIndexes.size.toFloat() / numberOfDays,
                    days = updatedHolidayIndexes,
                    daysDates = updatedHolidayIndexes.map { getDateByDayOfYear(it).toLocalDate() }
                )

                // seems like rates are correct
                if (holidayRecommendation.rate < 2) {
                    return holidayRecommendationList.map { it.daysDates }
                } else {
                    if (holidayRecommendationList.isEmpty()) {
                        holidayRecommendationList.add(holidayRecommendation)
                    } else {
                        if (holidayRecommendationList.last().rate != holidayRecommendation.rate
                        ) {
                            holidayRecommendationList.add(holidayRecommendation)
                        }
                    }
                }
            }
        }
    }

    private fun getHolidaysIndexesCloseToDate(wholeYear: List<Int>, holidayIndex: Int): List<Int> {
        val listOfNonWorkingDaysInARow = mutableListOf<Int>()
        listOfNonWorkingDaysInARow.add(holidayIndex)
        var nextIndex = holidayIndex

        while (true) {
            nextIndex++
            if (wholeYear.getOrNull(nextIndex) != 0 && wholeYear.getOrNull(nextIndex) != null) {
                listOfNonWorkingDaysInARow.add(nextIndex)
            } else {
                break
            }
        }

        nextIndex = holidayIndex

        while (true) {
            nextIndex--
            if (wholeYear.getOrNull(nextIndex) != 0 && wholeYear.getOrNull(nextIndex) != null) {
                listOfNonWorkingDaysInARow.add(nextIndex)
            } else {
                break
            }
        }
        return listOfNonWorkingDaysInARow.sorted()
    }

    // 0 - working day
    // 1 - weekends
    // 2 - public holidays
    // 3 - processed working days
    private fun makeListOfWorkingDaysOfTheYear(publicHolidays: List<PublicHoliday>): List<Int> {
        val wholeYear = MutableList(getNumberOfDaysInCurrentYear()) { 0 }
        getWeekendDaysIndexes().map { wholeYear[it - 1] = 1 }
        // if public holiday is on weekend, it is not counted as public holiday here
        getPublicHolidaysIndexes(publicHolidays).map {
            wholeYear[it - 1] = 2
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

    private fun getDateByDayOfYear(dayOfYear: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear + 1)
        return calendar.time
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
            Log.e(this.toString(), "Failed to parse date string: $dateString")
            null
        }
    }

    private fun List<List<LocalDate>>.toRecommendationList(): List<Recommendation> =
        this.map { Recommendation(it, isSelected = false, isVisible = true) }

    private enum class HolidayFinderDirection {
        LEFT, RIGHT
    }

    data class State(
        val isLoading: Boolean = true,
        val extendedPublicHolidays: List<ExtendedPublicHoliday> = emptyList(),
        val vacationDaysLeft: Int = 0
    ) : AbstractViewModel.State
}