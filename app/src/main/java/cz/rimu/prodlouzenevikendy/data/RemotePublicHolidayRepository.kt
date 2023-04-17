package cz.rimu.prodlouzenevikendy.data

import cz.rimu.prodlouzenevikendy.domain.PublicHolidaysRepository
import cz.rimu.prodlouzenevikendy.model.Api
import cz.rimu.prodlouzenevikendy.model.PublicHoliday

class RemotePublicHolidayRepository(
    private val api: Api
): PublicHolidaysRepository {
    override suspend fun getPublicHolidays(): List<PublicHoliday> {
        return api.getPublicHolidays()
    }
}