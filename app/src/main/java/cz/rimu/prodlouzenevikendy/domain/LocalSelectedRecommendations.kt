package cz.rimu.prodlouzenevikendy.domain

import cz.rimu.prodlouzenevikendy.model.Recommendation
import kotlinx.coroutines.flow.MutableStateFlow

interface LocalSelectedRecommendationsRepository {
    suspend fun getSelectedRecommendations(): MutableStateFlow<Set<Recommendation>>
    suspend fun addSelectedRecommendation(recommendation: Recommendation)
    suspend fun removeSelectedRecommendation(recommendation: Recommendation)
}