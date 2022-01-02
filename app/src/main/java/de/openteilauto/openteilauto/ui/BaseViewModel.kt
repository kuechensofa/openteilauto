package de.openteilauto.openteilauto.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.openteilauto.openteilauto.api.TeilautoApi
import de.openteilauto.openteilauto.model.AppError
import de.openteilauto.openteilauto.model.NetworkTeilautoDataSource
import de.openteilauto.openteilauto.model.TeilautoRepository
import kotlinx.coroutines.launch

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected val repository = TeilautoRepository(NetworkTeilautoDataSource(application.applicationContext))

    protected val notLoggedIn: MutableLiveData<Boolean> = MutableLiveData(false)
    protected val error: MutableLiveData<AppError?> = MutableLiveData()

    fun isNotLoggedIn(): LiveData<Boolean> {
        return notLoggedIn
    }

    fun getError(): LiveData<AppError?> {
        return error
    }

    fun logout() {
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis().toString()
            TeilautoApi.getInstance(getApplication()).logout(timestamp)
            notLoggedIn.postValue(true)
        }
    }
}