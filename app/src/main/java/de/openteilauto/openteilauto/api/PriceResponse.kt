package de.openteilauto.openteilauto.api

data class PriceResponse(
    val getPrice: PriceData?,
    val error: ErrorResponse?,
    val hasValidIdentity: Boolean
)

data class PriceData(
    val data: PricesData?
)

data class PricesData(
    val prices: Prices
)

data class Prices(
    val km: Price,
    val time: Price,
    val total: Price
)