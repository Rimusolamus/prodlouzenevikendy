package cz.rimu.prodlouzenevikendy.model

import retrofit2.http.GET

interface Api {
    @GET("PublicHolidays/2024/CZ")
    suspend fun getPublicHolidays(): List<PublicHoliday>
}