package cz.rimu.prodlouzenevikendy.presentation

import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import androidx.lifecycle.viewModelScope
import cz.rimu.prodlouzenevikendy.domain.LocalSelectedRecommendationsRepository
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
import java.time.LocalDate
import java.util.*

class HolidayListViewModel(
    private val publicHolidaysRepository: RemotePublicHolidaysRepository,
    private val selectedRecommendationsRepository: LocalSelectedRecommendationsRepository,
    context: Context
) : AbstractViewModel<HolidayListViewModel.State>(State()) {

    private val _publicHolidays = MutableStateFlow<List<PublicHoliday>>(emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            makeHolidayList(context)
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

    fun toggleTopRecommendation(recommendation: Recommendation) {
        viewModelScope.launch(Dispatchers.IO) {
            if (recommendation.isSelected) {
                selectedRecommendationsRepository.removeSelectedRecommendation(recommendation)
            } else {
                selectedRecommendationsRepository.addSelectedRecommendation(recommendation)
            }
        }
        state = state.copy(
            topTenPublicHolidays = state.topTenPublicHolidays.map {
                if (it == recommendation) {
                    it.copy(isSelected = !it.isSelected)
                } else {
                    it
                }
            }
        )
        // toggle calendar selection in extended public holidays
        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(
                extendedPublicHolidays = state.extendedPublicHolidays.map { extendedPublicHoliday ->
                    extendedPublicHoliday.copy(
                        recommendedDays = extendedPublicHoliday.recommendedDays.map { rec ->
                            if (rec == recommendation) {
                                rec.copy(isSelected = !rec.isSelected)
                            } else {
                                rec
                            }
                        }
                    )
                }
            )
        }
    }

    fun toggleCalendarSelection(
        publicHolidayIndex: Int,
        calendarIndex: Int,
        isAdding: Boolean
    ) {
        var rec: Recommendation? = null

        state = state.copy(
            extendedPublicHolidays = state.extendedPublicHolidays.mapIndexed { index, extendedPublicHoliday ->
                if (index == publicHolidayIndex) {
                    extendedPublicHoliday.copy(
                        recommendedDays = extendedPublicHoliday.recommendedDays.mapIndexed { i, recommendation ->
                            if (i == calendarIndex) {
                                rec = recommendation
                                if (isAdding) {
                                    viewModelScope.launch(Dispatchers.IO) {
                                        selectedRecommendationsRepository.addSelectedRecommendation(
                                            recommendation
                                        )
                                    }
                                } else {
                                    viewModelScope.launch(Dispatchers.IO) {
                                        selectedRecommendationsRepository.removeSelectedRecommendation(
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

        if (rec != null) {
            state = state.copy(
                topTenPublicHolidays = state.topTenPublicHolidays.map {
                    if (it == rec) {
                        it.copy(isSelected = isAdding)
                    } else {
                        it
                    }
                },
                extendedPublicHolidays = state.extendedPublicHolidays.map {
                    it.copy(
                        recommendedDays = it.recommendedDays.map { recommendation ->
                            if (recommendation == rec) {
                                recommendation.copy(isSelected = isAdding)
                            } else {
                                recommendation
                            }
                        }
                    )
                }
            )
        }
    }

    fun selectTab(selectedTabIndex: Int) {
        state = state.copy(
            selectedTabIndex = selectedTabIndex
        )
    }

    private fun getCountryCodeFromTelephony(context: Context): String? {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.networkCountryIso?.uppercase()
    }

    private suspend fun makeHolidayList(context: Context) {
        getCountryCodeFromTelephony(context)

        val holidays = publicHolidaysRepository.getPublicHolidays(
            getCountryCodeFromTelephony(context) ?: "CZ"
        )
            .sortedBy { parseDateString(it.date) ?: Date() }
        _publicHolidays.value = holidays

        val extHolidays = holidays.map { publicHoliday ->
            ExtendedPublicHoliday(
                name = publicHoliday.name,
                localName = publicHoliday.localName,
                date = parseDateString(publicHoliday.date),
                recommendedDays = listOf(),
                isVisible = false
            )
        }
        state = state.copy(extendedPublicHolidays = extHolidays)

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
                val leftAndRight = recommendToRight + recommendToLeft
                val updatedExtendedPublicHolidays = state.extendedPublicHolidays.toMutableList()
                if (holidayNumber in updatedExtendedPublicHolidays.indices) {
                    val updatedHoliday = updatedExtendedPublicHolidays[holidayNumber].copy(
                        recommendedDays = leftAndRight
                            .sortedBy { it.daysDates.size }
                            .toRecommendationList()
                    )
                    updatedExtendedPublicHolidays[holidayNumber] = updatedHoliday
                }

                state = state.copy(extendedPublicHolidays = updatedExtendedPublicHolidays)
            }
        }
        val uniqueDays = mutableSetOf<LocalDate>()
        val filteredTopTenPublicHolidays = state.extendedPublicHolidays
            .flatMap { it.recommendedDays }
            .sortedByDescending { it.rate }
            .filter { recommendation ->
                val isUnique = recommendation.days.none { it in uniqueDays }
                if (isUnique) {
                    uniqueDays.addAll(recommendation.days)
                }
                isUnique
            }
            .take(10)
        state = state.copy(
            topTenPublicHolidays = filteredTopTenPublicHolidays,
            isLoading = false
        )
    }

    private fun getRecommended(
        wholeYear: List<Int>,
        holidayIndexes: List<Int>,
        direction: HolidayFinderDirection
    ): List<HolidayRecommendation> {
        var mutableDirection = direction
        var holidayRecommendation = HolidayRecommendation()
        val holidayRecommendationList = mutableListOf<HolidayRecommendation>()
        var index = holidayIndexes.last()
        var numberOfDays = 0
        var directionChanged = 0

        val wholeYearMutable = wholeYear.toMutableList()
        while (true) {
            if (mutableDirection == HolidayFinderDirection.RIGHT) {
                index++
            } else {
                if (index == 0) {
                    return holidayRecommendationList
                }
                index--
            }
            if (wholeYearMutable.getOrNull(index) == 0 && wholeYearMutable.getOrNull(index) != null) {
                numberOfDays++
                wholeYearMutable[index] = 3
            }
            if (numberOfDays >= 1) {
                val updatedHolidayIndexes = getHolidaysIndexesCloseToDate(wholeYearMutable, index)

                index = if (mutableDirection == HolidayFinderDirection.RIGHT) {
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
                    if (directionChanged == 0) {
                        directionChanged++
                        mutableDirection = if (mutableDirection == HolidayFinderDirection.RIGHT) {
                            HolidayFinderDirection.LEFT
                        } else {
                            HolidayFinderDirection.RIGHT
                        }
                    } else {
                        return holidayRecommendationList
                    }
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

    private fun List<HolidayRecommendation>.toRecommendationList(): List<Recommendation> =
        this.map {
            Recommendation(
                it.daysDates,
                isSelected = false,
                rate = it.rate,
                isVisible = true
            )
        }

    private enum class HolidayFinderDirection {
        LEFT, RIGHT
    }

    data class State(
        val isLoading: Boolean = true,
        val selectedTabIndex: Int = 0,
        val extendedPublicHolidays: List<ExtendedPublicHoliday> = emptyList(),
        val topTenPublicHolidays: List<Recommendation> = emptyList(),
    ) : AbstractViewModel.State
}