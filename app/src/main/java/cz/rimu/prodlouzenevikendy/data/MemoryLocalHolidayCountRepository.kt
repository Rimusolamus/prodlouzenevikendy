package cz.rimu.prodlouzenevikendy.data

import cz.rimu.prodlouzenevikendy.domain.LocalHolidayCountRepository
import kotlinx.coroutines.flow.MutableStateFlow

class MemoryLocalHolidayCountRepository : LocalHolidayCountRepository {
    override var holidayCount: MutableStateFlow<Int> = MutableStateFlow(20)
}