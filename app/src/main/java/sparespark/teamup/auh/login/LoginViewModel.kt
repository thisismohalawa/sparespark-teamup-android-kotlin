package sparespark.teamup.auh.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.auh.AuthEvent
import sparespark.teamup.auh.BaseAuthViewModel
import sparespark.teamup.core.SIGN_IN_REQUEST_CODE
import sparespark.teamup.core.isEmailAddress
import sparespark.teamup.core.isValidPasswordLength
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.LoginResult
import sparespark.teamup.data.model.UIResource
import sparespark.teamup.data.model.User
import sparespark.teamup.data.repository.LoginRepository
import sparespark.teamup.data.repository.UserRepository

class LoginViewModel(
    private val loginRepo: LoginRepository, private val userRepo: UserRepository
) : BaseAuthViewModel() {

    internal val googleAuthAttempt = MutableLiveData<Unit>()
    internal val loginButtonText = MutableLiveData<UIResource>()
    internal val testCredentialAttempt = MutableLiveData<Unit>()

    private val userState = MutableLiveData<User?>()

    private val signableState = MutableLiveData<Boolean>()
    val signable: LiveData<Boolean> get() = signableState

    override fun handleEvent(event: AuthEvent<LoginResult>) {
        when (event) {
            is AuthEvent.GetAuthUser -> getAuthUser()
            is AuthEvent.GetTestUserCredential -> testCredentialAttempt.value = Unit
            is AuthEvent.OnAuthBtnClick -> googleAuthAttempt.value = Unit
            is AuthEvent.OnGoogleSignInResult -> onSignInResult(event.result)
            is AuthEvent.OnLoginBtnClick -> login(event.email, event.pass)
            else -> {}
        }
    }

    private fun getAuthUser() = viewModelScope.launch {
        showLoading()
        when (val result = loginRepo.getAuthUser()) {
            is Result.Error -> {
                showError(R.string.cannot_update_remote_data)
                showSignedOutState()
            }

            is Result.Value -> {
                userState.value = result.value
                checkCurrentSignState()
            }
        }
        hideLoading()
    }

    private fun pushAuthUserToRemote() = viewModelScope.launch {
        when (val result = userState.value?.let { userRepo.pushUserToRemoteServer(it) }) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = { showError(R.string.cannot_update_remote_data) })
            is Result.Value -> updateLocalUser()
            null -> showError(R.string.cannot_read_local_data)
        }
    }

    private fun updateLocalUser() = viewModelScope.launch {
        when (userState.value?.let { userRepo.updateLocalUser(it) }) {
            is Result.Error -> showError(R.string.cannot_update_local_entries)
            is Result.Value -> authUpdated()
            null -> showError(R.string.cannot_read_local_data)
        }
    }

    private fun onSignInResult(result: LoginResult) = viewModelScope.launch {
        showLoading()
        if (result.requestCode != SIGN_IN_REQUEST_CODE || result.userToken == null) {
            showError(R.string.unable_to_sign_in)
            return@launch
        }

        val createGoogleUserResult = loginRepo.signInGoogleUser(result.userToken)
        if (createGoogleUserResult is Result.Value) getAuthUser()
        else showError(R.string.unable_to_sign_in)
        hideLoading()
    }

    private fun login(email: String, pass: String) = viewModelScope.launch {
        if (!isValidInputs(email, pass)) return@launch
        showLoading()
        when (val result = loginRepo.signInWithEmailAndPass(email, pass)) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.unable_to_sign_in)
            })

            is Result.Value -> getAuthUser()
        }
        hideLoading()
    }

    private fun checkIfPushUserRequired() = viewModelScope.launch {
        val result = userRepo.checkIfPushRequired()
        if (result is Result.Value && result.value) pushAuthUserToRemote()
    }

    private fun checkCurrentSignState() {
        if (userState.value != null) {
            showSignedInState()
            checkIfPushUserRequired()
        } else showSignedOutState()
    }

    private fun showSignedInState() {
        signableState.value = false
        loginButtonText.value = UIResource.StringResource(R.string.signed_in_success)
    }

    private fun showSignedOutState() {
        signableState.value = true
        loginButtonText.value = UIResource.StringResource(R.string.login)
    }
}