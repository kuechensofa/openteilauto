package de.openteilauto.openteilauto.api.nominatim

import com.squareup.moshi.Json

data class ReverseGeoResponse(
    @Json(name = "place_id") val placeId: Long,
    @Json(name = "osm_type") val osmType: String,
    @Json(name = "osm_id") val osmId: Long,
    val lat: String,
    val lon: String,
    @Json(name = "display_name") val displayName: String,
    val address: Address
)

data class Address(
    @Json(name = "house_number") val houseNumber: String,
    val road: String,
    val suburb: String,
    @Json(name = "city_district") val cityDistrict: String,
    val city: String,
    val state: String,
    val postcode: String,
    val country: String,
    @Json(name = "country_code") val countryCode: String
)