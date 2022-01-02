package de.openteilauto.openteilauto.ui.login

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val firstName: String,
    val lastName: String
    //... other data fields that may be accessible to the UI
)