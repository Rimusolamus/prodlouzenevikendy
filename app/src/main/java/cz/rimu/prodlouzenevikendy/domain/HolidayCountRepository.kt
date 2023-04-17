package cz.rimu.prodlouzenevikendy.domain

/**
 * Repository to store the number of available holidays
 * @param holidayCount number of available holidays
 */
interface HolidayCountRepository {
    var holidayCount: Int
}