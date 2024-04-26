package cz.rimu.prodlouzenevikendy.data

import cz.rimu.prodlouzenevikendy.domain.LocalSelectedRecommendations
import cz.rimu.prodlouzenevikendy.model.Recommendation
import kotlinx.coroutines.flow.MutableStateFlow

class MemorySelectedRecommendations : LocalSelectedRecommendations {
    private val selectedRecommendations = MutableStateFlow<List<Recommendation>>(emptyList())

    override suspend fun getSelectedRecommendations(): List<Recommendation> {
        return selectedRecommendations.value
    }

    override suspend fun addSelectedRecommendation(recommendation: Recommendation) {
        selectedRecommendations.value += recommendation
    }

    override suspend fun removeSelectedRecommendation(recommendation: Recommendation) {
        selectedRecommendations.value -= recommendation
    }
}