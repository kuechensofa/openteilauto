package de.openteilauto.openteilauto.ui.search

import android.app.Application
import androidx.lifecycle.*
import de.openteilauto.openteilauto.model.*
import kotlinx.coroutines.launch

class SearchResultDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository =
        TeilautoRepository(NetworkTeilautoDataSource(application.applicationContext))

    private val timePrice: MutableLiveData<Int> = MutableLiveData()
    private val kmPrice: MutableLiveData<Int> = MutableLiveData()
    private val totalPrice: MutableLiveData<Int> = MutableLiveData()
    private val error: MutableLiveData<AppError> = MutableLiveData()
    private val notLoggedIn: MutableLiveData<Boolean> = MutableLiveData()

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

    fun getError(): LiveData<AppError?> {
        return error
    }

    fun isNotLoggedIn(): LiveData<Boolean> {
        return notLoggedIn
    }

}