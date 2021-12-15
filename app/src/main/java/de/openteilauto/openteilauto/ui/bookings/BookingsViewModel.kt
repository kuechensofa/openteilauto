package de.openteilauto.openteilauto.ui.bookings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.openteilauto.openteilauto.api.TeilautoApi
import de.openteilauto.openteilauto.model.Booking
import de.openteilauto.openteilauto.model.Error
import kotlinx.coroutines.launch
import retrofit2.HttpException

class BookingsViewModel : ViewModel() {
    private val bookings: MutableLiveData<List<Booking>> by lazy {
        MutableLiveData<List<Booking>>().also {
            loadBookings()
        }
    }
    private val error: MutableLiveData<Error?> = MutableLiveData()
    private val notLoggedIn: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getBookings(): LiveData<List<Booking>> {
        return bookings
    }

    fun getError(): LiveData<Error?> {
        return error
    }

    fun isNotLoggedIn(): LiveData<Boolean> {
        return notLoggedIn
    }

    private fun loadBookings() {
        viewModelScope.launch {
            try {
                val fieldMap = mapOf("firstEntry" to "0", "maxEntries" to "5",
                    "requestTimestamp" to "1639402392932", "driveMode" to "tA",
                    "platform" to "ios", "pg" to "pg", "version" to "22748",
                    "tracking" to "off")

                val response = TeilautoApi.teilautoService.getBookings(fieldMap)

                when {
                    !response.hasValidIdentity -> {
                        notLoggedIn.postValue(true)
                    }
                    response.listMyBookings?.data != null -> {
                        val receivedBookings = response.listMyBookings.data
                            .map { Booking(it.uid, it.title) }
                        bookings.postValue(receivedBookings)
                    }
                    response.error != null -> {
                        error.postValue(Error(response.error.message))
                    }
                    else -> {
                        error.postValue(Error("Unknown error"))
                    }
                }
            } catch (e: HttpException) {
                error.postValue(Error("Server Error!"))
            }
        }
    }
}