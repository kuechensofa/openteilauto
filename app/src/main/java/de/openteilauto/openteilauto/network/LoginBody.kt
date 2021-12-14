package de.openteilauto.openteilauto.network

data class LoginBody(
    val password: String,
    val membershipNo: String,
    val rememberMe: String = "true",
    val requestTimestamp: String = "1639351101910",
    val driveMode: String = "tA",
    val platform: String = "ios",
    val pg: String = "pg",
    val version: String = "22748",
    val tracking: String = "on"
)