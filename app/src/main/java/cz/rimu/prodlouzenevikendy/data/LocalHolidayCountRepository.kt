package cz.rimu.prodlouzenevikendy.data

import cz.rimu.prodlouzenevikendy.domain.HolidayCountRepository
import kotlinx.coroutines.flow.MutableStateFlow

class LocalHolidayCountRepository : HolidayCountRepository {
    override var holidayCount: MutableStateFlow<Int> = MutableStateFlow(20)
}