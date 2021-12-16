package de.openteilauto.openteilauto.model

import java.lang.Exception

open class ApiException(message: String) : Exception(message)

class NotLoggedInException : ApiException("User is not logged in")