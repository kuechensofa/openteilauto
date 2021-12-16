package de.openteilauto.openteilauto.model

class BookingRepository(private val bookingDataSource: BookingDataSource) {
    suspend fun getBookings(): List<Booking> {
        return bookingDataSource.getBookings()
    }
}