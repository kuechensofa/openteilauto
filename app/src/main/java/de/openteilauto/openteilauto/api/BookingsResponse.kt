package de.openteilauto.openteilauto.api

import com.squareup.moshi.Json

data class BookingsResponse(
    val listMyBookings: ListMyBookings?,
    val error: ErrorResponse?,
    val hasValidIdentity: Boolean
)

data class ListMyBookings(
    val data: List<Booking>?
)

data class Booking(
    val uid: String,
    val hash: String,
    val begin: Long,
    val end: Long,
    val vehicle: Vehicle,
    val startingPoint: Station,
    val destination: Station,
    val bookingText: String,
    val bookingKind: BookingKind,
    val showBookingTextInvoice: Boolean,
    val membershipCardUID: String,
    val bookingUID: String,
    @Json(name = "PetrolCardObject") val petrolCardObject: PetrolCardObject?,
    val needPetrolCard: Boolean,
    val isRunning: Boolean,
    val hasVehicleLockControl: Boolean,
    val timeBoundaries: TimeBoundaries,
    val extension: BookingExtension
)

data class BookingExtension(
    val maxUntil: String,
    val denialReason: String
)

data class BookingKind(
    val noShowCounter: String,
    val openEnd: Boolean,
    val instantAccess: Boolean,
    val ongoing: Boolean
)

data class PetrolCardObject(
    val petrolCard: PetrolCard
)

data class PetrolCard(
    val uid: String,
    val number: String,
    val pin: String,
    val description: String,
    val cardUID: String
)

data class TimeBoundaries(
    val maxBegin: Int,
    val minBegin: Int,
    val maxEnd: Int,
    val minEnd: Int
)

data class Mileage(
    val distance: String,
    val unit: String
)