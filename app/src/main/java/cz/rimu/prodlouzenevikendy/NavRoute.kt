package cz.rimu.prodlouzenevikendy

sealed class NavRoute(val path: String) {
    object HolidayList : NavRoute("holidayList")
    object SelectedHolidays : NavRoute("selectedHolidays")
}