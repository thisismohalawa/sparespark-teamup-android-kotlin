package sparespark.teamup.userprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.DELAY_NAV_RESTART
import sparespark.teamup.core.ROLE_EMPLOYEE
import sparespark.teamup.core.map.DEACTIVATED
import sparespark.teamup.core.wrapper.BaseViewModel
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.User
import sparespark.teamup.data.repository.UserRepository

private val TEMP_USER = User("", "tempX", "Temporary.", "", ROLE_EMPLOYEE, DEACTIVATED)

abstract class UserViewModel(
    private val userRepo: UserRepository
) : BaseViewModel<UserEvent>() {

    private val userState = MutableLiveData<User?>()
    val user: LiveData<User?> get() = userState

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState

    protected fun isUserExist() = userState.value?.uid?.isNotBlank() == true

    protected fun getUser(followAction: (() -> Unit)? = null) = viewModelScope.launch {
        when (val result = userRepo.getLocalUser()) {
            is Result.Error -> showError(R.string.cannot_read_local_data)

            is Result.Value -> result.value.let {
                if (it == null) userState.value = TEMP_USER
                else userState.value = it

                followAction?.invoke()
            }
        }
    }

    protected suspend fun userUpdated() {
        delay(DELAY_NAV_RESTART)
        updatedState.value = Unit
    }
}