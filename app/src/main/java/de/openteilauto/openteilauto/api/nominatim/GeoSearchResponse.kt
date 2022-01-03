package de.openteilauto.openteilauto.api.nominatim

import com.squareup.moshi.Json

data class GeoSearchResponse(
    @Json(name = "place_id") val placeId: Long,
    @Json(name = "osm_type") val osmType: String,
    @Json(name = "osm_id") val osmId: Long,
    val lat: String,
    val lon: String,
    @Json(name = "display_name") val displayName: String,
    @Json(name = "class") val class_: String,
    val type: String,
    val importance: Float
)