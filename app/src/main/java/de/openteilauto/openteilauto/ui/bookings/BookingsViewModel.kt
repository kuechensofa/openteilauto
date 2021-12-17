package de.openteilauto.openteilauto.ui.bookings

import android.app.Application
import androidx.lifecycle.*
import de.openteilauto.openteilauto.model.*
import kotlinx.coroutines.launch

class BookingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BookingRepository(NetworkBookingDataSource(application.applicationContext))

    private val bookings: MutableLiveData<List<Booking>> by lazy {
        MutableLiveData<List<Booking>>().also {
            loadBookings()
        }
    }
    private val error: MutableLiveData<AppError?> = MutableLiveData()
    private val notLoggedIn: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getBookings(): LiveData<List<Booking>> {
        return bookings
    }

    fun getError(): LiveData<AppError?> {
        return error
    }

    fun isNotLoggedIn(): LiveData<Boolean> {
        return notLoggedIn
    }

    fun refreshBookings() {
        loadBookings()
    }

    private fun loadBookings() {
        viewModelScope.launch {
            try {
                val receivedBookings = repository.getBookings()
                bookings.postValue(receivedBookings)
            } catch (e: NotLoggedInException) {
                notLoggedIn.postValue(true)
            } catch (e: ApiException) {
                error.postValue(AppError(e.message?: ""))
            }
        }
    }
}