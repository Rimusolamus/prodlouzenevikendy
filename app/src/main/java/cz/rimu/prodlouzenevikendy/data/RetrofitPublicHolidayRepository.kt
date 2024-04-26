package cz.rimu.prodlouzenevikendy.data

import cz.rimu.prodlouzenevikendy.domain.RemotePublicHolidaysRepository
import cz.rimu.prodlouzenevikendy.model.Api
import cz.rimu.prodlouzenevikendy.model.PublicHoliday
import java.time.LocalDate

class RetrofitPublicHolidayRepository(
    private val api: Api
): RemotePublicHolidaysRepository {
    override suspend fun getPublicHolidays(): List<PublicHoliday> {
        return api.getPublicHolidays(LocalDate.now().year.toString())
    }
}