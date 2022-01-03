package de.openteilauto.openteilauto.api.teilauto

data class UnlockResponse(
    val unlockVehicle: UnlockVehicle?,
    val error: ErrorResponse?,
    val hasValidIdentity: Boolean
)

data class UnlockVehicle(
    val data: UnlockVehicleData?
)

data class UnlockVehicleData(
    val successful: Boolean
)
