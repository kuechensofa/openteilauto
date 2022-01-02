package de.openteilauto.openteilauto.ui.bookings

import android.app.Application
import androidx.lifecycle.*
import de.openteilauto.openteilauto.model.*
import de.openteilauto.openteilauto.ui.BaseViewModel
import kotlinx.coroutines.launch

class BookingsViewModel(application: Application) : BaseViewModel(application) {
    private val bookings: MutableLiveData<List<Booking>> by lazy {
        MutableLiveData<List<Booking>>().also {
            loadBookings()
        }
    }

    fun getBookings(): LiveData<List<Booking>> {
        return bookings
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