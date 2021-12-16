package de.openteilauto.openteilauto.model

import android.content.Context
import de.openteilauto.openteilauto.api.TeilautoApi
import retrofit2.HttpException

interface BookingDataSource {
    suspend fun getBookings(): List<Booking>
    suspend fun getBooking(bookingUID: String): Booking?
    suspend fun unlockVehicle(bookingUID: String, appPIN: String): Boolean
    suspend fun lockVehicle(bookingUID: String): Boolean
}

class NetworkBookingDataSource(private val context: Context) : BookingDataSource {

    override suspend fun getBookings(): List<Booking> {
        try {
            val timestamp = System.currentTimeMillis().toString()
            val fieldMap = mapOf("firstEntry" to "0", "maxEntries" to "5",
                "requestTimestamp" to timestamp, "driveMode" to "tA",
                "platform" to "ios", "pg" to "pg", "version" to "22748",
                "tracking" to "off")

            val response = TeilautoApi.getInstance(context).getBookings(fieldMap)

            when {
                !response.hasValidIdentity -> {
                    throw NotLoggedInException()
                }
                response.listMyBookings?.data != null -> {
                    return transformBookings(response.listMyBookings.data)
                }
                response.error != null -> {
                    if (response.error.message != "") {
                        throw ApiException(response.error.message)
                    } else {
                        throw ApiException("Unknown API error")
                    }
                }
                else -> {
                    throw ApiException("Unknown error")
                }
            }
        } catch (e: HttpException) {
            throw ApiException("Server Error!")
        }
    }

    override suspend fun getBooking(bookingUID: String): Booking? {
        val bookings = getBookings()
        for (booking in bookings) {
            if (booking.uid == bookingUID) {
                return booking
            }
        }
        return null
    }

    override suspend fun unlockVehicle(bookingUID: String, appPIN: String): Boolean {
        try {
            val timestamp = System.currentTimeMillis().toString()
            val fieldMap = mapOf("bookingUID" to bookingUID, "appPIN" to appPIN,
                "requestTimestamp" to timestamp, "driveMode" to "tA",
                "platform" to "ios", "pg" to "pg", "version" to "22748",
                "tracking" to "off")
            val response = TeilautoApi.getInstance(context).unlockVehicle(fieldMap)

            when {
                !response.hasValidIdentity -> {
                    throw NotLoggedInException()
                }
                response.unlockVehicle?.data != null -> {
                    return response.unlockVehicle.data.successful
                }
                response.error != null -> {
                    throw ApiException(response.error.message)
                }
                else -> {
                    throw ApiException("Unknown error")
                }
            }
        } catch (e: HttpException) {
            throw ApiException("Server Error!")
        }
    }

    override suspend fun lockVehicle(bookingUID: String): Boolean {
        try {
            val timestamp = System.currentTimeMillis().toString()
            val fieldMap = mapOf("bookingUID" to bookingUID,
                "requestTimestamp" to timestamp, "driveMode" to "tA",
                "platform" to "ios", "pg" to "pg", "version" to "22748",
                "tracking" to "off")
            val response = TeilautoApi.getInstance(context).lockVehicle(fieldMap)

            when {
                !response.hasValidIdentity -> {
                    throw NotLoggedInException()
                }
                response.lockVehicle?.data != null -> {
                    return response.lockVehicle.data.successful
                }
                response.error != null -> {
                    throw ApiException(response.error.message)
                }
                else -> {
                    throw ApiException("Unknown error")
                }
            }
        } catch (e: HttpException) {
            throw ApiException("Server Error!")
        }
    }

    private fun transformBookings(receivedBookings: List<de.openteilauto.openteilauto.api.Booking?>)
        : List<Booking> {
        val bookings = mutableListOf<Booking>()
        for (receivedBooking in receivedBookings) {
            if (receivedBooking == null) {
                continue
            }
            val booking = Booking.fromReceivedBooking(receivedBooking)
            bookings.add(booking)
        }

        return bookings.toList()
    }
}