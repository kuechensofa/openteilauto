package de.openteilauto.openteilauto.model

import de.openteilauto.openteilauto.api.SearchData
import java.util.*

data class SearchResult(
    val begin: Date,
    val end: Date,
    val vehicle: Vehicle,
    val startingPoint: Station,
    val rating: Int,
    val timeOverlapping: Boolean
) {
    companion object {
        fun fromApiSearchData(receivedSearchData: SearchData): SearchResult {
            val begin = Date(receivedSearchData.begin * 1000)
            val end = Date(receivedSearchData.end * 1000)
            val vehicle = Vehicle.fromReceivedVehicle(receivedSearchData.vehicle)
            val startingPoint = Station.fromReceivedStation(receivedSearchData.startingPoint)

            return SearchResult(
                begin,
                end,
                vehicle,
                startingPoint,
                receivedSearchData.rating,
                receivedSearchData.timeOverlapping
            )
        }
    }
}