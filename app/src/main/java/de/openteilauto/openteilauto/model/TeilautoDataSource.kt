package de.openteilauto.openteilauto.model

import android.content.Context
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.api.SearchData
import de.openteilauto.openteilauto.api.TeilautoApi
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*

interface TeilautoDataSource {
    suspend fun getBookings(): List<Booking>
    suspend fun getBooking(bookingUID: String): Booking?
    suspend fun unlockVehicle(bookingUID: String, appPIN: String): Boolean
    suspend fun lockVehicle(bookingUID: String): Boolean
    suspend fun search(begin: Date, end: Date, vehicleClasses: List<VehicleClass>, maxResults: Int,
        address: String?, geoPos: GeoPos?, radius: Int): List<SearchResult>
    suspend fun getPrice(begin: Date, end: Date, estimatedKm: Int, vehicleUID: String?,
        vehiclePoolUID: String?): Price
    suspend fun book(begin: Date, end: Date, vehicleUID: String?, vehiclePoolUID: String?,
        bookingText: String = "", showBookingTextInvoice: Boolean = true): String
    suspend fun cancelBooking(bookingUID: String, sendConfirmationEmail: Boolean = true)
}

class NetworkTeilautoDataSource(private val context: Context) : TeilautoDataSource {

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
                        throw ApiException(context.resources.getString(R.string.unknown_error))
                    }
                }
                else -> {
                    throw ApiException(context.resources.getString(R.string.unknown_error))
                }
            }
        } catch (e: HttpException) {
            throw ApiException(context.resources.getString(R.string.server_error))
        } catch (e: SocketTimeoutException) {
            throw ApiException(context.resources.getString(R.string.network_error))
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
                    throw ApiException(context.resources.getString(R.string.unknown_error))
                }
            }
        } catch (e: HttpException) {
            throw ApiException(context.resources.getString(R.string.server_error))
        } catch (e: SocketTimeoutException) {
            throw ApiException(context.resources.getString(R.string.network_error))
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
                    throw ApiException(context.resources.getString(R.string.unknown_error))
                }
            }
        } catch (e: HttpException) {
            throw ApiException(context.resources.getString(R.string.server_error))
        } catch (e: SocketTimeoutException) {
            throw ApiException(context.resources.getString(R.string.network_error))
        }
    }

    override suspend fun search(
        begin: Date,
        end: Date,
        vehicleClasses: List<VehicleClass>,
        maxResults: Int,
        address: String?,
        geoPos: GeoPos?,
        radius: Int
    ): List<SearchResult> {
        check(address != null || geoPos != null)

        try {
            val timestamp = System.currentTimeMillis().toString()
            val response = TeilautoApi.getInstance(context).search(
                (begin.time / 1000).toString(),
                (end.time / 1000).toString(),
                vehicleClasses.map{ it.classCode.toString() },
                maxResults.toString(),
                address,
                geoPos?.lat,
                geoPos?.lon,
                radius.toString(),
                timestamp
            )
            when {
                !response.hasValidIdentity -> {
                    throw NotLoggedInException()
                }
                response.error != null -> {
                    throw ApiException(response.error.message)
                }
                response.search?.data != null -> {
                    return transformSearchResults(response.search.data)
                }
                else -> {
                    throw ApiException(context.resources.getString(R.string.unknown_error))
                }
            }
        } catch (e: HttpException) {
            throw ApiException(context.resources.getString(R.string.server_error))
        } catch (e: SocketTimeoutException) {
            throw ApiException(context.resources.getString(R.string.network_error))
        }
    }

    override suspend fun getPrice(
        begin: Date,
        end: Date,
        estimatedKm: Int,
        vehicleUID: String?,
        vehiclePoolUID: String?
    ): Price {
        check(vehicleUID != null || vehiclePoolUID != null)

        try {
            val timestamp = System.currentTimeMillis().toString()
            val response = TeilautoApi.getInstance(context).getPrice(
                (begin.time / 1000).toString(),
                (end.time / 1000).toString(),
                estimatedKm.toString(),
                vehicleUID,
                vehiclePoolUID,
                timestamp
            )
            when {
                !response.hasValidIdentity -> {
                    throw NotLoggedInException()
                }
                response.error != null -> {
                    throw ApiException(response.error.message)
                }
                response.getPrice?.data != null -> {
                    return Price(
                        response.getPrice.data.prices.time.amount,
                        response.getPrice.data.prices.km.amount,
                        response.getPrice.data.prices.total.amount
                    )
                }
                else -> {
                    throw ApiException(context.resources.getString(R.string.unknown_error))
                }
            }
        } catch (e: HttpException) {
            throw ApiException(context.resources.getString(R.string.server_error))
        } catch (e: SocketTimeoutException) {
            throw ApiException(context.resources.getString(R.string.network_error))
        }
    }

    override suspend fun book(
        begin: Date,
        end: Date,
        vehicleUID: String?,
        vehiclePoolUID: String?,
        bookingText: String,
        showBookingTextInvoice: Boolean
    ): String {
        check(vehicleUID != null || vehiclePoolUID != null)

        try {
            val timestamp = System.currentTimeMillis().toString()
            val response = TeilautoApi.getInstance(context).book(
                (begin.time / 1000).toString(),
                (end.time / 1000).toString(),
                vehicleUID,
                vehiclePoolUID,
                bookingText,
                if (showBookingTextInvoice) "true" else "false",
                timestamp
            )
            when {
                !response.hasValidIdentity -> {
                    throw NotLoggedInException()
                }
                response.error != null -> {
                    throw ApiException(response.error.message)
                }
                response.book?.data != null -> {
                    return response.book.data.bookingUID.toString()
                }
                else -> {
                    throw ApiException(context.resources.getString(R.string.unknown_error))
                }
            }
        } catch (e: HttpException) {
            throw ApiException(context.resources.getString(R.string.server_error))
        } catch (e: SocketTimeoutException) {
            throw ApiException(context.resources.getString(R.string.network_error))
        }
    }

    override suspend fun cancelBooking(bookingUID: String, sendConfirmationEmail: Boolean) {
        try {
            val timestamp = System.currentTimeMillis().toString()
            val response = TeilautoApi.getInstance(context).cancelBooking(
                bookingUID,
                if (sendConfirmationEmail) "true" else "false",
                timestamp
            )
            when {
                !response.hasValidIdentity -> {
                    throw NotLoggedInException()
                }
                response.error != null -> {
                    throw ApiException(response.error.message)
                }
                response.cancelBooking?.data != null -> {
                    return
                }
                else -> {
                    throw ApiException(context.resources.getString(R.string.unknown_error))
                }
            }
        } catch (e: HttpException) {
            throw ApiException(context.resources.getString(R.string.server_error))
        } catch (e: SocketTimeoutException) {
            throw ApiException(context.resources.getString(R.string.network_error))
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

    private fun transformSearchResults(receivedSearchResults: List<SearchData>): List<SearchResult> {
        return receivedSearchResults.map { SearchResult.fromApiSearchData(it) }
    }
}