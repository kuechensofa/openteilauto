package de.openteilauto.openteilauto.ui.search

import android.annotation.SuppressLint
import android.app.Application
import android.location.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import de.openteilauto.openteilauto.model.*
import de.openteilauto.openteilauto.ui.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "SearchViewModel"

class SearchViewModel(application: Application) : BaseViewModel(application), LocationListener {
    private val searchResults: MutableLiveData<List<SearchResult>> = MutableLiveData()

    private val location: MutableLiveData<Location> by lazy {
        MutableLiveData<Location>().also {
            updateLocation()
        }
    }
    private val geocode: MutableLiveData<Address> = MutableLiveData()

    fun search(
        address: String,
        beginDate: Date,
        endDate: Date,
        categories: List<VehicleClass>,
        geoPos: GeoPos
    ) {
        viewModelScope.launch {
            try {
                val radius = 5000
                val searchResult = repository.search(
                    address,
                    beginDate,
                    endDate,
                    categories,
                    geoPos,
                    radius
                )

                searchResults.postValue(searchResult)
            } catch (e: NotLoggedInException) {
                notLoggedIn.postValue(true)
            } catch (e: ApiException) {
                error.postValue(AppError(e.message ?: ""))
            }
        }
    }

    fun getSearchResults(): LiveData<List<SearchResult>> {
        return searchResults
    }

    fun getLocation(): LiveData<Location> {
        return location
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        val locationManager =
            getApplication<Application>().getSystemService(AppCompatActivity.LOCATION_SERVICE)
                    as LocationManager

        viewModelScope.launch {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    10f,
                    this@SearchViewModel
                )
            } else {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    10f,
                    this@SearchViewModel
                )
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        this.location.postValue(location)
        Log.d(TAG, location.toString())
    }

    fun requestGeocode(location: Location) {
        viewModelScope.launch {
            val geocoder = Geocoder(getApplication())
            val geocodes = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            if (geocodes.size == 1) {
                geocode.postValue(geocodes[0])
            }
        }
    }

    fun getGeocode(): LiveData<Address> {
        return geocode
    }
}