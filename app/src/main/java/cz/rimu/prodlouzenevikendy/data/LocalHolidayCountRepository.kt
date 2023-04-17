package cz.rimu.prodlouzenevikendy.data

import cz.rimu.prodlouzenevikendy.domain.HolidayCountRepository

class LocalHolidayCountRepository : HolidayCountRepository {
    override var holidayCount: Int = 0
}