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

data class Vehicle(
    val name: String,
    val licensePlate: String,
    val model: String,
    val brand: String,
    val vehicleUID: String?,
    val poolUID: String?,
    val rentalObjectID: String,
    val showType: String,
    val additionalInfo: VehicleAdditionalInfo,
    val station: Station,
    val driveMode: String,
    val title: String,
    val mileage: Mileage?,
    val imagePath: String
)

data class VehicleAdditionalInfo(
    val seats: Int?,
    val doors: Int?,
    val colour: String?,
    val fuelType: String?,
    val changeLevel: Int?,
    val chargeStatus: String?,
    val fuelCategory: String?,
    val equipment: List<Equipment>?
)

data class Station(
    val uid: Int,
    val name: String,
    val shorthand: String,
    val hasFixedParking: Boolean,
    val geoPos: GeoPos
)

data class Equipment(
    val uid: String,
    val short: String,
    val long: String
)