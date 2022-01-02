package de.openteilauto.openteilauto.model

import java.util.*

class TeilautoRepository(private val teilautoDataSource: TeilautoDataSource) {
    suspend fun getBookings(): List<Booking> {
        return teilautoDataSource.getBookings()
    }

    suspend fun getBooking(bookingUID: String): Booking? {
        return teilautoDataSource.getBooking(bookingUID)
    }

    suspend fun unlockVehicle(bookingUID: String, pin: String): Boolean {
        return teilautoDataSource.unlockVehicle(bookingUID, pin)
    }

    suspend fun lockVehicle(bookingUID: String): Boolean {
        return teilautoDataSource.lockVehicle(bookingUID)
    }

    suspend fun search(
        address: String,
        beginDate: Date,
        endDate: Date,
        categories: List<VehicleClass>,
        geoPos: GeoPos,
        radius: Int
    ): List<SearchResult> {
        return teilautoDataSource.search(beginDate, endDate, categories, 10, address,
            geoPos, radius)
    }

    suspend fun getPrice(
        begin: Date,
        end: Date,
        estimatedKm: Int,
        vehicleUID: String?,
        vehiclePoolUID: String?
    ): Price {
        return teilautoDataSource.getPrice(begin, end, estimatedKm, vehicleUID, vehiclePoolUID)
    }
}