package de.openteilauto.openteilauto.api.teilauto

data class BookingCancellationResponse(
    val cancelBooking: CancelBooking?,
    val error: ErrorResponse?,
    val hasValidIdentity: Boolean
)

data class CancelBooking(
    val data: CancelBookingData?
)

data class CancelBookingData(
    val bookingUID: String
)