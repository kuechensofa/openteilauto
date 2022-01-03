package de.openteilauto.openteilauto.ui.search

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.api.nominatim.NominatimApi
import de.openteilauto.openteilauto.model.*
import de.openteilauto.openteilauto.ui.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*

class SearchViewModel(application: Application) : BaseViewModel(application), LocationListener {
    private val nominatimApi = NominatimApi.getInstance()
    private val searchResults: MutableLiveData<List<SearchResult>> = MutableLiveData()
    private var locationManager: LocationManager? = null
    private var locationFound: Boolean = false

    private val location: MutableLiveData<GeoPos> = MutableLiveData()
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

    fun getLocation(): LiveData<GeoPos> {
        return location
    }

    fun updateLocation() {
        locationManager =
            getApplication<Application>().getSystemService(AppCompatActivity.LOCATION_SERVICE)
                    as LocationManager

        viewModelScope.launch {
            if (ActivityCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                // no location permissions granted -> display message to user
                if (!locationFound) {
                    error.postValue(AppError(getApplication<Application>()
                        .resources.getString(R.string.manual_location_warning)))
                }
            } else {
                if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
                    val lastLocation = locationManager?.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER
                    )
                    val lastPos = GeoPos(
                        lastLocation?.longitude.toString(),
                        lastLocation?.latitude.toString()
                    )
                    location.postValue(lastPos)
                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        10f,
                        this@SearchViewModel
                    )
                    updateGeocode(lastPos)
                } else {
                    val lastLocation = locationManager?.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER
                    )
                    val lastPos = GeoPos(
                        lastLocation?.longitude.toString(),
                        lastLocation?.latitude.toString()
                    )
                    location.postValue(lastPos)
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1000,
                        10f,
                        this@SearchViewModel
                    )
                    updateGeocode(lastPos)
                }
                locationFound = true
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        val pos = GeoPos(
            location.longitude.toString(),
            location.latitude.toString()
        )
        this.location.postValue(pos)
        updateGeocode(pos)
        Log.d(this.javaClass.name, location.toString())
        locationFound = true
    }

    private fun updateGeocode(location: GeoPos) {
        viewModelScope.launch {
            val geoResponse = nominatimApi.reverse(location.lat, location.lon)
            Log.d(this@SearchViewModel.javaClass.name, geoResponse.toString())
            geocode.postValue(geoResponse.displayName)
        }
    }

    fun getGeocode(): LiveData<String> {
        return geocode
    }

    fun locationByAddress(address: String) {
        viewModelScope.launch {
            locationManager?.removeUpdates(this@SearchViewModel)
            val nominatimResponse = nominatimApi.search(address)

            if (nominatimResponse.isNotEmpty()) {
                val locationResult = nominatimResponse[0]
                val foundLocation = GeoPos(locationResult.lon, locationResult.lat)
                location.postValue(foundLocation)
                updateGeocode(foundLocation)
                locationFound = true
            } else {
                Log.d(this@SearchViewModel.javaClass.name,
                    "No location found for query '${address}'")
                error.postValue(AppError(getApplication<Application>()
                    .resources.getString(R.string.location_not_found)))
            }
        }
    }

    fun getDistance(pos: GeoPos, searchResult: SearchResult): Float {
        val ownLocation = Location("")
        ownLocation.latitude = pos.lat.toDouble()
        ownLocation.longitude = pos.lon.toDouble()

        val stationLocation = Location("")
        stationLocation.latitude = searchResult.startingPoint.geoPos.lat.toDouble()
        stationLocation.longitude = searchResult.startingPoint.geoPos.lon.toDouble()

        return ownLocation.distanceTo(stationLocation)
    }

    fun sortResultsByDistance(results: List<SearchResult>, geoPos: GeoPos): List<SearchResult> {
        return results.sortedBy { getDistance(geoPos, it) }
    }
}