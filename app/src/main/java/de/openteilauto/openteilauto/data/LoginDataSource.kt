package de.openteilauto.openteilauto.data

import de.openteilauto.openteilauto.data.model.LoggedInUser
import de.openteilauto.openteilauto.network.TeilautoApi
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    suspend fun login(membershipNo: String, password: String): Result<LoggedInUser> {
        try {
            val result = TeilautoApi.retrofitService.login(password, membershipNo)
            System.out.println(result)
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(),
                "12345", "Jane", "Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    suspend fun logout() {
        // TODO: revoke authentication
    }
}