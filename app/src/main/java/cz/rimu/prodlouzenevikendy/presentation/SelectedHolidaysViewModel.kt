package cz.rimu.prodlouzenevikendy.presentation

import androidx.lifecycle.viewModelScope
import cz.rimu.prodlouzenevikendy.domain.LocalSelectedRecommendations
import cz.rimu.prodlouzenevikendy.model.Recommendation
import cz.rimu.tools.presentation.AbstractViewModel
import kotlinx.coroutines.launch

class SelectedHolidaysViewModel(
    private val selectedRecommendations: LocalSelectedRecommendations
): AbstractViewModel<SelectedHolidaysViewModel.State>(State()) {
    init {
        viewModelScope.launch {
            state = state.copy(selectedHolidays = selectedRecommendations.getSelectedRecommendations())
        }
    }

    data class State(
        val selectedHolidays: List<Recommendation>? = emptyList()
    ) : AbstractViewModel.State
}