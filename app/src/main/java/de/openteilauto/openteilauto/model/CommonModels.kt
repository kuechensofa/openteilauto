package de.openteilauto.openteilauto.model

data class GeoPos(
    val lon: String,
    val lat: String
) {
    companion object {
        fun fromReceivedGeoPos(receivedGeoPos: de.openteilauto.openteilauto.api.GeoPos): GeoPos {
            return GeoPos(receivedGeoPos.lon, receivedGeoPos.lat)
        }
    }
}