package de.openteilauto.openteilauto.api

data class LoginResponse(
    val login: LoginLogin,
    val error: ErrorResponse?,
)

data class LoginLogin(
    val data: LoginData?,
)

data class LoginData(
    val membershipNo: String,
    val uid: String,
    val salutation: LoginUserSalutation,
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