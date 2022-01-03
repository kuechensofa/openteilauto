package de.openteilauto.openteilauto.api.teilauto

data class BookingRequestResponse(
    val book: BookingResponse?,
    val error: ErrorResponse?,
    val hasValidIdentity: Boolean
)

data class BookingResponse(
    val data: BookingData?
)

data class BookingData(
    val bookingUID: Long
)
