package sparespark.teamup.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.wrapper.BaseViewModel
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.ErrorUIResource
import sparespark.teamup.data.model.UIResource
import sparespark.teamup.data.model.User
import sparespark.teamup.data.repository.UserRepository

private const val DELAY_LOGIN = 2000L

class HomeViewModel(
    private val userRepo: UserRepository
) : BaseViewModel<HomeViewEvent>() {

    private val userState = MutableLiveData<User?>()

    internal val loginAttempt = MutableLiveData<Unit>()
    internal val requestPermissionsAttempt = MutableLiveData<Unit>()
    internal val actionToolbarTitle = MutableLiveData<ErrorUIResource>()

    override fun handleEvent(event: HomeViewEvent) {
        when (event) {
            is HomeViewEvent.OnStartGetUser -> getUser()
        }
    }

    private fun getUser() = viewModelScope.launch {
        updateToolbarTitle(R.string.check_auth_user)
        when (val result = userRepo.getLocalUser()) {
            is Result.Error -> {
                updateToolbarTitle(resMsg = R.string.cannot_read_local_data, isError = true)
                delay(DELAY_LOGIN)
                moveToLoginView()
            }

            is Result.Value -> {
                userState.value = result.value

                if (userState.value == null) {
                    updateToolbarTitle(resMsg = R.string.cannot_find_auth_user, isError = true)
                    checkIfLoginRequired()
                } else {
                    updateToolbarTitle(R.string.read_remote_user)
                    checkRemoteUser()
                }
            }
        }
    }

    private fun checkIfLoginRequired() = viewModelScope.launch {
        when (val result = userRepo.checkIfLoginRequired()) {
            is Result.Value -> if (result.value) {
                delay(DELAY_LOGIN)
                moveToLoginView()
            } else updateToolbarTitle(resMsg = R.string.unauth_summary, isError = true)

            is Result.Error -> updateToolbarTitle(
                resMsg = R.string.unauth_summary,
                isError = true
            )
        }
    }

    private fun checkRemoteUser() = viewModelScope.launch {
        when (val result = userRepo.getRemoteUser()) {
            is Result.Error -> result.error.message.actionExceptionMsg(offline = {
                updateToolbarTitle(R.string.no_internet, true)
            }, unauthorised = {
                updateToolbarTitle(R.string.unauthorized, true)
            }, error = {
                updateToolbarTitle(resMsg = R.string.error_retrieve_data, isError = true)
                moveToLoginView()
            })

            is Result.Value -> result.value.let { rUser ->
                if (rUser == userState.value) {
                    updateToolbarTitle(resMsg = R.string.synced_success)
                } else {
                    userState.value = rUser
                    updateLocalUser()
                }

                if (isActiveUser()) {
                    updateLastLogin()
                    requestPermissions()
                } else updateToolbarTitle(R.string.deactivated, true)
            }
        }
    }

    private fun updateLocalUser() = viewModelScope.launch {
        updateToolbarTitle(R.string.setup_sync)
        if (userState.value?.let { userRepo.updateLocalUser(it) } is Result.Value) updateToolbarTitle(
            resMsg = R.string.synced_success
        )
        else updateToolbarTitle(
            resMsg = R.string.error_user_syncing, isError = true
        )
    }

    private fun isActiveUser(): Boolean = userState.value?.activated == true

    private fun updateLastLogin() = viewModelScope.launch {
        userRepo.updateLastLogin()
    }

    private fun requestPermissions() {
        requestPermissionsAttempt.value = Unit
    }

    private fun moveToLoginView() {
        loginAttempt.value = Unit
    }

    private fun updateToolbarTitle(resMsg: Int, isError: Boolean = false) {
        actionToolbarTitle.value = ErrorUIResource(
            uiResource = UIResource.StringResource(resMsg), isError = isError
        )
    }
}