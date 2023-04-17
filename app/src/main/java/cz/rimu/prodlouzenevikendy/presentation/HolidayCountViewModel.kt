package cz.rimu.prodlouzenevikendy.presentation

import androidx.lifecycle.ViewModel
import cz.rimu.prodlouzenevikendy.domain.HolidayCountRepository

class HolidayCountViewModel(
    private val holidayCountRepository: HolidayCountRepository
): ViewModel() {
    fun onHolidayCountChanged(holidayCount: Int) {
        holidayCountRepository.holidayCount = holidayCount
    }
}