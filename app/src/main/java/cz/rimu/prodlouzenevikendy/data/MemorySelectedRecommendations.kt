package cz.rimu.prodlouzenevikendy.data

import cz.rimu.prodlouzenevikendy.domain.LocalSelectedRecommendationsRepository
import cz.rimu.prodlouzenevikendy.model.Recommendation
import kotlinx.coroutines.flow.MutableStateFlow

class MemorySelectedRecommendationsRepository : LocalSelectedRecommendationsRepository {
    private val selectedRecommendations = MutableStateFlow<Set<Recommendation>>(emptySet())

    override suspend fun getSelectedRecommendations(): MutableStateFlow<Set<Recommendation>> {
        return selectedRecommendations
    }

    override suspend fun addSelectedRecommendation(recommendation: Recommendation) {
        selectedRecommendations.value += recommendation
    }

    override suspend fun removeSelectedRecommendation(recommendation: Recommendation) {
        selectedRecommendations.value -= recommendation.copy(isSelected = false)
    }
}