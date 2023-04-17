package cz.rimu.prodlouzenevikendy.domain

import cz.rimu.prodlouzenevikendy.model.PublicHoliday

interface PublicHolidaysRepository {
    suspend fun getPublicHolidays(): List<PublicHoliday>
}