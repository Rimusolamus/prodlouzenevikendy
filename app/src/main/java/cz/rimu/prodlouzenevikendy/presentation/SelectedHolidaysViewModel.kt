package cz.rimu.prodlouzenevikendy.presentation

import androidx.lifecycle.viewModelScope
import cz.rimu.prodlouzenevikendy.domain.LocalSelectedRecommendationsRepository
import cz.rimu.prodlouzenevikendy.model.Recommendation
import cz.rimu.tools.presentation.AbstractViewModel
import kotlinx.coroutines.launch

class SelectedHolidaysViewModel(
    private val selectedRecommendations: LocalSelectedRecommendationsRepository
): AbstractViewModel<SelectedHolidaysViewModel.State>(State()) {
    init {
        viewModelScope.launch {
            selectedRecommendations.getSelectedRecommendations().collect {
                state = state.copy(selectedHolidays = it.toList())
            }
        }
    }

    data class State(
        val selectedHolidays: List<Recommendation>? = emptyList()
    ) : AbstractViewModel.State
}