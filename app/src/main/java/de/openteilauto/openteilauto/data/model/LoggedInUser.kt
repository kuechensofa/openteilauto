package de.openteilauto.openteilauto.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val uid: String,
    val membershipNo: String,
    val firstName: String,
    val lastName: String
)