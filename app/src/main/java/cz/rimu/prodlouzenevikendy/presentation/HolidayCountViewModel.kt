package cz.rimu.prodlouzenevikendy.presentation

import androidx.lifecycle.viewModelScope
import cz.rimu.prodlouzenevikendy.domain.LocalHolidayCountRepository
import cz.rimu.tools.presentation.AbstractViewModel
import kotlinx.coroutines.launch

class HolidayCountViewModel(
    private val holidayCountRepository: LocalHolidayCountRepository
) : AbstractViewModel<HolidayCountViewModel.State>(State()) {

    init {
        viewModelScope.launch {
            holidayCountRepository.holidayCount.collect {
                state = state.copy(
                    holidayCount = it
                )
            }
        }
    }

    fun onHolidayCountChanged(holidayCount: Int) {
        holidayCountRepository.holidayCount.value = holidayCount
    }

    data class State(
        val holidayCount: Int = 0
    ) : AbstractViewModel.State
}