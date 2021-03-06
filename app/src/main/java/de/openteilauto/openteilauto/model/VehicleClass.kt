package de.openteilauto.openteilauto.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class VehicleClass(val classCode: Int): Parcelable {
    MINI(24),
    SMALL(25),
    DELIVERY_VAN(26),
    COMPACT(27),
    MINI_VAN(29),
    MEDIUM_SIZED(28),
    TRANSPORTER(30),
    BUS(31)
}