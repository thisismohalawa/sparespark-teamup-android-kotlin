package sparespark.teamup.userprofile.profiledetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.MAX_INPUT_NAME_DIG
import sparespark.teamup.core.isValidLength
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.repository.UserRepository
import sparespark.teamup.userprofile.UserEvent
import sparespark.teamup.userprofile.UserViewModel

class ProfileDetailViewModel(
    val userRepo: UserRepository
) : UserViewModel(userRepo) {

    internal val nameTxtValidateState = MutableLiveData<Unit>()

    override fun handleEvent(event: UserEvent) {
        when (event) {
            is UserEvent.GetUser -> getUser()

            is UserEvent.OnUpdateBtnClick -> updateRemoteUser(
                event.name, event.phone
            )

            else -> Unit
        }
    }

    private fun updateRemoteUser(name: String, phone: String) = viewModelScope.launch {
        if (!name.isValidLength(maxDig = MAX_INPUT_NAME_DIG)) {
            nameTxtValidateState.value = Unit
            return@launch
        }
        showLoading()
        when (val result = userRepo.updateRemoteUser(name, phone)) {
            is Result.Error -> result.error.message.checkExceptionMsg(
                error = {
                    showError(R.string.cannot_update_remote_data)
                })

            is Result.Value -> userUpdated()
        }
        hideLoading()
    }
}