package cz.rimu.prodlouzenevikendy.domain

import cz.rimu.prodlouzenevikendy.model.PublicHoliday

interface RemotePublicHolidaysRepository {
    suspend fun getPublicHolidays(country: String): List<PublicHoliday>
}