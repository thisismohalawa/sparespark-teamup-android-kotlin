package sparespark.teamup.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T> : ViewModel() {
    abstract fun handleEvent(event: T)

    private val loadingState = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = loadingState

    protected fun showLoading() {
        loadingState.value = true
    }

    protected fun hideLoading() {
        loadingState.value = false
    }
}
