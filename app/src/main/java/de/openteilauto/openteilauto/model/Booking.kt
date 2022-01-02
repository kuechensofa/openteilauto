package de.openteilauto.openteilauto.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

data class Booking(
    val uid: String,
    val begin: Date,
    val end: Date,
    val vehicle: Vehicle,
    val petrolCard: PetrolCard?,
    val startingPoint: Station,
    val destination: Station
) {
    companion object {
        fun fromReceivedBooking(receivedBooking: de.openteilauto.openteilauto.api.Booking): Booking {
            val begin = Date(receivedBooking.begin * 1000)
            val end = Date(receivedBooking.end * 1000)
            val vehicle = Vehicle.fromReceivedVehicle(receivedBooking.vehicle)


            val receivedPetrolCard = receivedBooking.petrolCardObject?.petrolCard
            val petrolCard = if (receivedPetrolCard != null) {
                PetrolCard.fromReceivedPetrolCard(receivedPetrolCard)
            } else {
                null
            }

            val startingPoint = Station.fromReceivedStation(receivedBooking.startingPoint)
            val destination = Station.fromReceivedStation(receivedBooking.destination)

            return Booking(receivedBooking.uid, begin, end, vehicle, petrolCard, startingPoint,
                destination)
        }
    }

    fun isCurrent(currentTime: Date): Boolean {
        return currentTime in begin..end
    }
}

@Parcelize
data class Vehicle(
    val uid: String,
    val name: String,
    val licensePlate: String,
    val model: String,
    val brand: String,
    val station: Station,
    val title: String,
    val imageUrl: Uri,
    val vehicleUID: String?,
    val vehiclePoolUID: String?
): Parcelable {
    companion object {
        fun fromReceivedVehicle(receivedVehicle: de.openteilauto.openteilauto.api.Vehicle): Vehicle {
            val station = Station.fromReceivedStation(receivedVehicle.station)
            val imageUrl = Uri.parse(receivedVehicle.imagePath)
            val uid = receivedVehicle.vehicleUID ?: receivedVehicle.poolUID
            return Vehicle(
                uid!!,
                receivedVehicle.name,
                receivedVehicle.licensePlate,
                receivedVehicle.model,
                receivedVehicle.brand,
                station,
                receivedVehicle.title,
                imageUrl,
                receivedVehicle.vehicleUID,
                receivedVehicle.poolUID)
        }
    }
}

@Parcelize
data class Station(
    val uid: Int,
    val name: String,
    val shorthand: String,
    val hasFixedParking: Boolean,
    val geoPos: GeoPos
): Parcelable {
    companion object {
        fun fromReceivedStation(receivedStation: de.openteilauto.openteilauto.api.Station): Station {
            val stationGeoPos = GeoPos.fromReceivedGeoPos(receivedStation.geoPos)
            return Station(receivedStation.uid, receivedStation.name,
                receivedStation.shorthand, receivedStation.hasFixedParking,
                stationGeoPos)
        }
    }
}

data class PetrolCard(
    val uid: String,
    val number: String,
    val pin: String,
    val description: String,
    val cardUID: String
) {
    companion object {
        fun fromReceivedPetrolCard(receivedPetrolCard: de.openteilauto.openteilauto.api.PetrolCard): PetrolCard {
            return PetrolCard(receivedPetrolCard.uid, receivedPetrolCard.number,
                receivedPetrolCard.pin, receivedPetrolCard.description,
                receivedPetrolCard.cardUID)
        }
    }
}