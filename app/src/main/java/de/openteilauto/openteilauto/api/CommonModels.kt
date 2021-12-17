package de.openteilauto.openteilauto.api

data class ErrorResponse(
    val id: String,
    val message: String,
    val name: String,
    val hal2IDS: List<String>,
    val errorMessageRegex: String,
    val extra: ErrorExtra?
)

data class ErrorExtra(
    val faultCode: FaultCode?
)

data class FaultCode(
    val faultCode: String,
    val faultString: String,
    val detail: List<FaultDetail>
)

data class FaultDetail(
    val errorCode: Int,
    val errorText: String,
    val errorAddText: String
)

data class GeoPos(
    val lon: String,
    val lat: String
)