package cz.rimu.prodlouzenevikendy.model

import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("PublicHolidays/{year}/CZ")
    suspend fun getPublicHolidays(@Path("year") year: String): List<PublicHoliday>
}