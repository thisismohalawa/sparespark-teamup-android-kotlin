package sparespark.teamup.auh

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import sparespark.teamup.core.isEmailAddress
import sparespark.teamup.core.isValidPasswordLength
import sparespark.teamup.core.wrapper.BaseViewModel
import sparespark.teamup.data.model.LoginResult

abstract class BaseAuthViewModel : BaseViewModel<AuthEvent<LoginResult>>() {

    internal val emailTxtValidateState = MutableLiveData<Unit>()
    internal val passwordTxtValidateState = MutableLiveData<Unit>()

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState

    protected fun authUpdated() {
        updatedState.value = Unit
    }

    protected fun isValidInputs(email: String, pass: String): Boolean =
        if (!email.isEmailAddress()) {
            emailTxtValidateState.value = Unit
            false
        } else if (!pass.isValidPasswordLength()) {
            passwordTxtValidateState.value = Unit
            false
        } else true

}