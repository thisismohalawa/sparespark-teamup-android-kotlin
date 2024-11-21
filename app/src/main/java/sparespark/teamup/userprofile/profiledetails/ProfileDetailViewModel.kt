package sparespark.teamup.userprofile.profiledetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.MAX_INPUT_NAME_DIG
import sparespark.teamup.core.MIN_INPUT_NAME_DIG
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.repository.UserRepository
import sparespark.teamup.userprofile.UserEvent
import sparespark.teamup.userprofile.UserViewModel

class ProfileDetailViewModel(
    private val userRepo: UserRepository
) : UserViewModel(userRepo) {

    internal val nameTxtValidateState = MutableLiveData<Unit>()

    override fun handleEvent(event: UserEvent) {
        when (event) {
            is UserEvent.GetCurrentUser -> getUser()

            is UserEvent.OnUpdateBtnClick -> updateRemoteUser(
                event.name, event.phone
            )

            else -> Unit
        }
    }

    private fun updateRemoteUser(name: String, phone: String) = viewModelScope.launch {
        if (name.length in MIN_INPUT_NAME_DIG..MAX_INPUT_NAME_DIG) {
            showLoading()
            when (val result = userRepo.updateRemoteUser(name, phone)) {
                is Result.Error -> result.error.message.checkExceptionMsg(error = {
                    showError(R.string.cannot_update_remote_data)
                })

                is Result.Value -> userUpdated()
            }
            hideLoading()
        } else {
            nameTxtValidateState.value = Unit
            return@launch
        }
    }
}