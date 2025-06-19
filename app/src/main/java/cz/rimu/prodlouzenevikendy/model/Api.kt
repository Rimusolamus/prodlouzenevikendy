package cz.rimu.prodlouzenevikendy.model

import retrofit2.http.GET
import retrofit2.http.Path

interface Api {
    @GET("PublicHolidays/{year}/{country_code}")
    suspend fun getPublicHolidays(@Path("year") year: String, @Path("country_code") countryCode: String): List<PublicHoliday>
}