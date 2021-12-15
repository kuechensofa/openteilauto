package de.openteilauto.openteilauto.api

data class ErrorResponse(
    val id: String,
    val message: String,
    val name: String,
    val hal2IDS: List<String>,
    val errorMessageRegex: String
)

data class GeoPos(
    val lon: String,
    val lat: String
)