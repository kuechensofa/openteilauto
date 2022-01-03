package de.openteilauto.openteilauto.api.teilauto

data class SearchResponse(
    val search: Search?,
    val error: ErrorResponse?,
    val hasValidIdentity: Boolean
)

data class Search(
    val data: List<SearchData>?
)

data class SearchData(
    val uid: Int,
    val hash: String?,
    val begin: Long,
    val end: Long,
    val price: PriceList,
    val vehicle: Vehicle,
    val startingPoint: Station,
    val rating: Int,
    val timeOverlapping: Boolean
)

data class MinBookingPrice(
    val minBookingCost: Price?,
    val minBookingCostNetto: Price?,
    val minEnd: String,
    val minDuration: String
)