package cz.rimu.prodlouzenevikendy.domain

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Repository to store the number of available holidays
 * @param holidayCount number of available holidays
 */
interface LocalHolidayCountRepository {
    val holidayCount: MutableStateFlow<Int>
}