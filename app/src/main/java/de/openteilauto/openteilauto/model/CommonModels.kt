package de.openteilauto.openteilauto.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GeoPos(
    val lon: String,
    val lat: String
): Parcelable {
    companion object {
        fun fromReceivedGeoPos(receivedGeoPos: de.openteilauto.openteilauto.api.GeoPos): GeoPos {
            return GeoPos(receivedGeoPos.lon, receivedGeoPos.lat)
        }
    }
}