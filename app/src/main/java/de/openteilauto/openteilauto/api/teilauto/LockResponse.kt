package de.openteilauto.openteilauto.api.teilauto

data class LockResponse(
    val lockVehicle: LockVehicle?,
    val error: ErrorResponse?,
    val hasValidIdentity: Boolean
)

data class LockVehicle(
    val data: LockVehicleData?
)

data class LockVehicleData(
    val successful: Boolean
)