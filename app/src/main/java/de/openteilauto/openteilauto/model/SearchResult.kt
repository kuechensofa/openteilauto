package de.openteilauto.openteilauto.model

import android.os.Parcelable
import de.openteilauto.openteilauto.api.SearchData
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class SearchResult(
    val uid: Int,
    val begin: Date,
    val end: Date,
    val vehicle: Vehicle,
    val startingPoint: Station,
    val rating: Int,
    val timeOverlapping: Boolean,
    val timeCost: Int
): Parcelable {
    companion object {
        fun fromApiSearchData(receivedSearchData: SearchData): SearchResult {
            val begin = Date(receivedSearchData.begin * 1000)
            val end = Date(receivedSearchData.end * 1000)
            val vehicle = Vehicle.fromReceivedVehicle(receivedSearchData.vehicle)
            val startingPoint = Station.fromReceivedStation(receivedSearchData.startingPoint)

            return SearchResult(
                receivedSearchData.uid,
                begin,
                end,
                vehicle,
                startingPoint,
                receivedSearchData.rating,
                receivedSearchData.timeOverlapping,
                receivedSearchData.price.timeCost.amount
            )
        }
    }
}