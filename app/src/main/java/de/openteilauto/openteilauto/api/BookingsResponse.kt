package de.openteilauto.openteilauto.api

import org.json.JSONObject

data class BookingsResponse(
    val listMyBookings: List<String>?,
    val error: ErrorResponse?,
    val debug: JSONObject?,
    val hasValidIdentity: Boolean,
    val tracking: String,
    val config: Config
)