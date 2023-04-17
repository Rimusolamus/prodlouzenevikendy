package cz.rimu.prodlouzenevikendy.model

import com.squareup.moshi.Json

data class PublicHoliday(
    @field:Json(name = "data") val date: String,
    @field:Json(name = "localName") val localName: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "countryCode") val countryCode: String,
    @field:Json(name = "fixed") val fixed: Boolean,
    @field:Json(name = "global") val global: Boolean,
    @field:Json(name = "counties") val counties: List<String>?,
    @field:Json(name = "launchYear") val launchYear: Int?,
    @field:Json(name = "types") val types: List<String>
)