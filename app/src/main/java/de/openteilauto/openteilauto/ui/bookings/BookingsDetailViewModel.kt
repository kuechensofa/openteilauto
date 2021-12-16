package de.openteilauto.openteilauto.ui.bookings

import android.app.Application
import androidx.lifecycle.*
import de.openteilauto.openteilauto.model.*
import kotlinx.coroutines.launch

class BookingsDetailViewModel(application: Application, private val bookingUID: String)
        : AndroidViewModel(application) {
    private val repository =
        BookingRepository(NetworkBookingDataSource(application.applicationContext))

    private val booking: MutableLiveData<Booking> by lazy {
        MutableLiveData<Booking>().also {
            loadBooking(bookingUID)
        }
    }

    private val error: MutableLiveData<AppError?> = MutableLiveData()
    private val notLoggedIn: MutableLiveData<Boolean> = MutableLiveData(false)
    private val unlockSuccessful: MutableLiveData<Boolean> = MutableLiveData()
    private val lockSuccessful: MutableLiveData<Boolean> = MutableLiveData()

    fun getBooking(): LiveData<Booking> {
        return booking
    }

    fun getError(): LiveData<AppError?> {
        return error
    }

    fun getNotLoggedIn(): LiveData<Boolean> {
        return notLoggedIn
    }

    fun isUnlockSuccessful(): LiveData<Boolean> {
        return unlockSuccessful
    }

    fun isLockSuccessful(): LiveData<Boolean> {
        return lockSuccessful
    }

    fun unlockVehicle(pin: String) {
        viewModelScope.launch {
            try {
                val unlockSuccessfulResponse = repository.unlockVehicle(bookingUID, pin)
                unlockSuccessful.postValue(unlockSuccessfulResponse)
            } catch (e: NotLoggedInException) {
                notLoggedIn.postValue(true)
            } catch (e: ApiException) {
                error.postValue(AppError(e.message?: ""))
            }
        }
    }

    fun lockVehicle() {
        viewModelScope.launch {
            try {
                val lockSuccessfulResponse = repository.lockVehicle(bookingUID)
                lockSuccessful.postValue(lockSuccessfulResponse)
            } catch (e: NotLoggedInException) {
                notLoggedIn.postValue(true)
            } catch (e: ApiException) {
                error.postValue(AppError(e.message?: ""))
            }
        }
    }

    private fun loadBooking(bookingUID: String) {
        viewModelScope.launch {
            try {
                val receivedBooking = repository.getBooking(bookingUID)
                booking.postValue(receivedBooking)
            } catch (e: NotLoggedInException) {
                notLoggedIn.postValue(true)
            } catch (e: ApiException) {
                error.postValue(AppError(e.message?: ""))
            }
        }
    }
}

class BookingsDetailViewModelFactory(private val application: Application, private val bookingUID: String) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Application::class.java, String::class.java)
            .newInstance(application, bookingUID)
    }
}