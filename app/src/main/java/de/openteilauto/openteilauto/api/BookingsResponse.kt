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
    val station: Station,
    val driveMode: String,
    val title: String,
    val extension: BookingExtension,
    val startingPoint: Station,
    val destination: Station,
    val bookingText: String,
    val bookingKind: BookingKind,
    val showBookingTextInvoice: Boolean,
    val membershipCardUID: String,
    val bookingUID: String,
    @Json(name = "PetrolCardObject") val petrolCardObject: PetrolCard?,
    val needPetrolCard: Boolean,
    val isRunning: Boolean,
    val hasVehicleLockControl: Boolean,
    val timeBoundaries: TimeBoundaries
)

data class Vehicle(
    val name: String,
    val licensePlate: String,
    val model: String,
    val brand: String,
    val vehicleUID: String,
    val rentalObjectID: String,
    val showType: String,
    val additionalInfo: VehicleAdditionalInfo
)

data class VehicleAdditionalInfo(
    val seats: Int,
    val doors: Int,
    val colour: String,
    val fuelType: String,
    val changeLevel: Int,
    val chargeStatus: String,
    val fuelCategory: String
)

data class Station(
    val uid: Int,
    val name: String,
    val shorthand: String,
    val hasFixedParking: Boolean,
    val geoPos: GeoPos
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

data class PetrolCard(
    val uid: String?
)

data class TimeBoundaries(
    val maxBegin: Int,
    val minBegin: Int,
    val maxEnd: Int,
    val minEnd: Int
)