package de.openteilauto.openteilauto.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.openteilauto.openteilauto.api.TeilautoApi
import de.openteilauto.openteilauto.model.Error
import de.openteilauto.openteilauto.model.User
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel : ViewModel() {

    val loggedInUser: MutableLiveData<User?> = MutableLiveData()
    val loginError: MutableLiveData<Error?> = MutableLiveData()

    fun login(membershipNo: String, password: String) {
        viewModelScope.launch {
            try {
                val fieldMap = mapOf("password" to password,
                    "membershipNo" to membershipNo, "rememberMe" to "true",
                    "requestTimestamp" to "1639351101910", "driveMode" to "tA",
                    "platform" to "ios", "pg" to "pg", "version" to "22748", "tracking" to "off")

                val response = TeilautoApi.teilautoService.login(fieldMap)

                when {
                    response.login.data != null -> {
                        val user = User(response.login.data.membershipNo,
                            response.login.data.salutation.firstname,
                            response.login.data.salutation.lastname)
                        loggedInUser.postValue(user)
                    }
                    response.error != null -> {
                        val error = Error(response.error.message)
                        loginError.postValue(error)
                    }
                    else -> {
                        val error = Error("Unknown login error")
                        loginError.postValue(error)
                    }
                }
            } catch (e: HttpException) {
                loginError.postValue(Error("Login failed!"))
            }
        }
    }
}