package de.openteilauto.openteilauto.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.api.TeilautoApi
import de.openteilauto.openteilauto.model.AppError
import de.openteilauto.openteilauto.model.User
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val loggedInUser: MutableLiveData<User?> = MutableLiveData()
    val loginError: MutableLiveData<AppError?> = MutableLiveData()

    fun login(membershipNo: String, password: String) {
        viewModelScope.launch {
            try {
                val timestamp = System.currentTimeMillis().toString()
                val fieldMap = mapOf("password" to password,
                    "membershipNo" to membershipNo, "rememberMe" to "true",
                    "requestTimestamp" to timestamp, "driveMode" to "tA",
                    "platform" to "ios", "pg" to "pg", "version" to "22748", "tracking" to "off")

                val response = TeilautoApi.getInstance((getApplication() as Application)
                    .applicationContext).login(fieldMap)

                when {
                    response.login.data != null -> {
                        val user = User(response.login.data.membershipNo,
                            response.login.data.salutation.firstname,
                            response.login.data.salutation.lastname)
                        loggedInUser.postValue(user)
                    }
                    response.error != null -> {
                        val error = AppError(response.error.message)
                        loginError.postValue(error)
                    }
                    else -> {
                        val error = AppError((getApplication() as Application)
                            .resources.getString(R.string.unknown_login_error))
                        loginError.postValue(error)
                    }
                }
            } catch (e: HttpException) {
                loginError.postValue(AppError((getApplication() as Application)
                    .resources.getString(R.string.login_failed)))
            } catch (e: SocketTimeoutException) {
                loginError.postValue(AppError((getApplication() as Application)
                    .resources.getString(R.string.network_error)))
            }
        }
    }
}