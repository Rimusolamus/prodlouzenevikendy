package cz.rimu.prodlouzenevikendy.domain

import cz.rimu.prodlouzenevikendy.model.Recommendation

interface LocalSelectedRecommendations {
    suspend fun getSelectedRecommendations(): List<Recommendation>?
    suspend fun addSelectedRecommendation(recommendation: Recommendation)
    suspend fun removeSelectedRecommendation(recommendation: Recommendation)
}