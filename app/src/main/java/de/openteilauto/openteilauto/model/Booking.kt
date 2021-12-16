package de.openteilauto.openteilauto.model

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
            val begin = Date(receivedBooking.begin)
            val end = Date(receivedBooking.end)
            val vehicle = Vehicle.fromReceivedVehicle(receivedBooking.vehicle)


            val receivedPetrolCard = receivedBooking.petrolCardObject?.petrolCard
            val petrolCard = if (receivedPetrolCard != null) {
                PetrolCard(receivedPetrolCard.uid, receivedPetrolCard.number,
                    receivedPetrolCard.pin, receivedPetrolCard.description,
                    receivedPetrolCard.cardUID)
            } else {
                null
            }

            val startingPoint = Station.fromReceivedStation(receivedBooking.startingPoint)
            val destination = Station.fromReceivedStation(receivedBooking.destination)

            return Booking(receivedBooking.uid, begin, end, vehicle, petrolCard, startingPoint,
                destination)
        }
    }
}

data class Vehicle(
    val uid: String,
    val name: String,
    val licensePlate: String,
    val model: String,
    val brand: String,
    val station: Station,
    val title: String
) {
    companion object {
        fun fromReceivedVehicle(receivedVehicle: de.openteilauto.openteilauto.api.Vehicle): Vehicle {
            val station = Station.fromReceivedStation(receivedVehicle.station)
            return Vehicle(receivedVehicle.vehicleUID, receivedVehicle.name,
                receivedVehicle.licensePlate, receivedVehicle.model, receivedVehicle.brand,
                station, receivedVehicle.title)
        }
    }
}

data class Station(
    val uid: Int,
    val name: String,
    val shorthand: String,
    val hasFixedParking: Boolean,
    val geoPos: GeoPos
) {
    companion object {
        fun fromReceivedStation(receivedStation: de.openteilauto.openteilauto.api.Station): Station {
            val stationGeoPos = GeoPos.fromReceivedGeoPos(receivedStation.geoPos)
            return Station(receivedStation.uid, receivedStation.name,
                receivedStation.shorthand, receivedStation.hasFixedParking,
                stationGeoPos)
        }
    }
}

data class GeoPos(
    val lon: String,
    val lat: String
) {
    companion object {
        fun fromReceivedGeoPos(receivedGeoPos: de.openteilauto.openteilauto.api.GeoPos): GeoPos {
            return GeoPos(receivedGeoPos.lon, receivedGeoPos.lat)
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