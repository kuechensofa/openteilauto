package de.openteilauto.openteilauto.model

class BookingRepository(private val bookingDataSource: BookingDataSource) {
    suspend fun getBookings(): List<Booking> {
        return bookingDataSource.getBookings()
    }

    suspend fun getBooking(bookingUID: String): Booking? {
        return bookingDataSource.getBooking(bookingUID)
    }
}