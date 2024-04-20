package cz.rimu.prodlouzenevikendy.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.rimu.prodlouzenevikendy.domain.HolidayCountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HolidayCountViewModel(
    private val holidayCountRepository: HolidayCountRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        State(
            holidayCount = 0
        )
    )
    val state = _state

    init {
        viewModelScope.launch {
            holidayCountRepository.holidayCount.collect {
                _state.value = _state.value.copy(
                    holidayCount = it
                )
            }
        }
    }

    fun onHolidayCountChanged(holidayCount: Int) {
        holidayCountRepository.holidayCount.value = holidayCount
    }

    data class State(
        val holidayCount: Int
    )
}