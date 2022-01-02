package de.openteilauto.openteilauto.ui.search

import android.app.Application
import androidx.lifecycle.*
import de.openteilauto.openteilauto.model.*
import de.openteilauto.openteilauto.ui.BaseViewModel
import kotlinx.coroutines.launch

class SearchResultDetailViewModel(application: Application) : BaseViewModel(application) {
    private val timePrice: MutableLiveData<Int> = MutableLiveData()
    private val kmPrice: MutableLiveData<Int> = MutableLiveData()
    private val totalPrice: MutableLiveData<Int> = MutableLiveData()
    private val bookingUid: MutableLiveData<String> = MutableLiveData()

    fun updatePriceEstimation(searchResult: SearchResult, estimatedKm: Int) {
        viewModelScope.launch {
            try {
                val prices = repository.getPrice(
                    searchResult.begin, searchResult.end, estimatedKm,
                    searchResult.vehicle.vehicleUID, searchResult.vehicle.vehiclePoolUID
                )
                timePrice.postValue(prices.timePrice)
                kmPrice.postValue(prices.kmPrice)
                totalPrice.postValue(prices.totalPrice)
            } catch (e: NotLoggedInException) {
                notLoggedIn.postValue(true)
            } catch (e: ApiException) {
                error.postValue(AppError(e.message ?: ""))
            }
        }
    }

    fun getTimePrice(): LiveData<Int> {
        return timePrice
    }

    fun getKmPrice(): LiveData<Int> {
        return kmPrice
    }

    fun getTotalPrice(): LiveData<Int> {
        return totalPrice
    }

    fun getBookingUid(): LiveData<String> {
        return bookingUid
    }

    fun book(searchResult: SearchResult) {
        viewModelScope.launch {
            try {
                val bookingUid = repository.book(
                    searchResult.begin,
                    searchResult.end,
                    searchResult.vehicle.vehicleUID,
                    searchResult.vehicle.vehiclePoolUID
                )
                this@SearchResultDetailViewModel.bookingUid.postValue(bookingUid)
            } catch (e: NotLoggedInException) {
                notLoggedIn.postValue(true)
            } catch (e: ApiException) {
                error.postValue(AppError(e.message?: ""))
            }
        }
    }
}