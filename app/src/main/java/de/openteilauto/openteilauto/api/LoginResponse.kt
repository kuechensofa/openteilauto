package de.openteilauto.openteilauto.api

import org.json.JSONObject

data class LoginResponse(
    val login: LoginLogin,
    val error: LoginError?,
    val debug: JSONObject
)

data class LoginLogin(
    val data: LoginData?,
)

data class LoginData(
    val membershipNo: String,
    val uid: String,
    val salutation: LoginUserSalutation,
    val station: LoginUserStation,
    val contract: LoginUserContract
)

data class LoginUserSalutation(
    val salutation: String,
    val title: String,
    val firstname: String,
    val lastname: String,
    val lastname2: String,
    val lastname3: String?,
    val gender: String,
    val birthdate: String
)

data class LoginUserStation(
    val uid: String,
    val name: String,
    val shorthand: String,
    val hasFixedParking: Boolean,
    val geoPos: GeoPos
)

data class GeoPos(
    val lon: String,
    val lat: String
)

data class LoginUserContract(
    val contractUID: String,
    val productUID: String,
    val begin: String,
    val tariffName: String,
    val contractRtf: String,
    val deductible: LoginUserContractDeductible,
    val membershipCard: List<JSONObject>,
    val preferCompanyVehicle: Boolean,
    val address: Address,
    val product: Product,
    val paymentMethod: String,
    val dutyTripAllowed: Boolean,
    val dutyTripDefault: Boolean,
    val wantReallyCard: Boolean,
    val invoiceDelivery: InvoiceDelivery,
    val company: Company,
    val profile: Profile,
    val driversLicense: DriversLicense,
    val brokerProfileUid: String,
    val needsChat: Boolean,
    val helpChatEnabled: Boolean,
    val hasOpenVoucher: Boolean,
    val hasFwFProduct: Boolean,
    val depositDeficit: Int,
    val securityDeposit: Int,
    val cfOnly: Boolean,
    val homeStationId: Int,
    val lastname: String,
    val firstname: String,
    val email: String,
    val mobile: String
)

data class DriversLicense(
    val date: String,
    val class_: String,
    val number: String,
    val issuer: String,
    val agreementForTelephoneConfirmation: Boolean,
    val expiry: String
)

data class Profile(
    val status: String,
    val canDrive: Boolean,
    val canBook: Boolean,
    val isMainCustomer: Boolean,
    val isLocked: Boolean,
    val subs: Int,
    val mainCustomerName: String?,
    val mainCustomerNo: String?
)

data class InvoiceDelivery(
    val type: String,
    val price: Price
)

data class Address(
    val street: String,
    val number: String,
    val zip: String,
    val city: City,
    val district: String?
)

data class City(
    val uid: Int,
    val name: String,
    val country: Country,
    val hasCompanyPreferredVehicle: Boolean?
)

data class Country(
    val uid: String,
    val name: String,
    val iso: String
)

data class Product(
    val uid: String,
    val description: String,
    val tariffUID: String,
    val tariffName: String,
    val price: Price,
    val membershipFee: Price,
    val company: Company,
    val authentication: List<String>,
    val paymentMethod: List<String>,
    val productPml: List<ProductPml>
)

data class ProductPml(
    val text: String,
    val price: Price,
    val paymentFrequency: String,
    val isCost: Boolean,
    val isBailment: Boolean,
    val isCredit: Boolean
)

data class Price(
    val amount: Int,
    val currency: String,
    val vat: String,
    val tax: Int,
    val priceNetto: Int,
    val displayNettoPrice: Boolean
)

data class Company(
    val uid: String,
    val shortName: String,
    val fullName: String
)

data class LoginUserContractDeductible(
    val amount: Int,
    val currency: String,
    val vat: Int,
    val tax: Int,
    val priceNetto: Int,
    val displayNettoPrice: Boolean
)

data class LoginError(
    val id: String,
    val message: String,
    val name: String,
    val hal2IDS: List<String>,
    val errorMessageRegex: String
)