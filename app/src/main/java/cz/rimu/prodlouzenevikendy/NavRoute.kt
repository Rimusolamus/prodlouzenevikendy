package cz.rimu.prodlouzenevikendy

sealed class NavRoute(val path: String) {
    object HolidayCount : NavRoute("holidayCount")
    object HolidayList : NavRoute("holidayList")
}
