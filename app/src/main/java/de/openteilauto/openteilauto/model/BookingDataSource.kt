package de.openteilauto.openteilauto.model

import android.content.Context
import de.openteilauto.openteilauto.api.TeilautoApi
import retrofit2.HttpException
import java.util.*

interface BookingDataSource {
    suspend fun getBookings(): List<Booking>
}

class MockBookingDataSource : BookingDataSource {
    private val bookings: List<Booking>

    init {
        val station = Station(1, "Test Station", "Test", true,
            GeoPos("1", "1")
        )
        val vehicle = Vehicle("1", "Test Auto", "A-BC-123", "Test Model",
            "Test Brand", station, "Test Auto")
        val petrolCard = PetrolCard("1", "1234-5678-9012-3456", "1234",
            "Test Card", "1")
        val begin1 = Date(2021, 12, 1, 12, 0)
        val end1 = Date(2021, 12, 1, 14, 0)
        val begin2 = Date(2021, 12, 3, 10, 0)
        val end2 = Date(2021, 12, 3, 15, 30)

        bookings = listOf(
            Booking("1", begin1, end1, vehicle, petrolCard, station, station),
            Booking("2", begin2, end2, vehicle, null, station, station))
    }

    override suspend fun getBookings(): List<Booking> {
        return bookings
    }
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