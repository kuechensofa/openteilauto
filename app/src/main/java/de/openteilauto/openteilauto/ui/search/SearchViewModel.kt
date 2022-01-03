package de.openteilauto.openteilauto.ui.search

import android.annotation.SuppressLint
import android.app.Application
import android.location.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import de.openteilauto.openteilauto.api.nominatim.NominatimApi
import de.openteilauto.openteilauto.model.*
import de.openteilauto.openteilauto.ui.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*

class SearchViewModel(application: Application) : BaseViewModel(application), LocationListener {
    private val searchResults: MutableLiveData<List<SearchResult>> = MutableLiveData()

    private val location: MutableLiveData<Location> by lazy {
        MutableLiveData<Location>().also {
            updateLocation()
        }
    }
    private val geocode: MutableLiveData<String> = MutableLiveData()

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
                val lastLocation = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                location.postValue(lastLocation)
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    10f,
                    this@SearchViewModel
                )
            } else {
                val lastLocation = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                location.postValue(lastLocation)
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
        updateGeocode(location)
        Log.d(this.javaClass.name, location.toString())
    }

    private fun updateGeocode(location: Location) {
        viewModelScope.launch {
            val nominatimApi = NominatimApi.getInstance()
            val geoResponse = nominatimApi.reverse(location.latitude.toString(), location.longitude.toString())
            Log.d(this@SearchViewModel.javaClass.name, geoResponse.toString())
            geocode.postValue(geoResponse.displayName)
        }
    }

    fun getGeocode(): LiveData<String> {
        return geocode
    }
}