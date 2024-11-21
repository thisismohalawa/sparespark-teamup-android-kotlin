package sparespark.teamup.teamlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.base.BaseViewModel
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.team.Team
import sparespark.teamup.data.repository.TeamRepository

class TeamViewModel(
    private val teamRepo: TeamRepository
) : BaseViewModel<TeamEvent>() {

    private val teamListState = MutableLiveData<List<Team>>()
    val teamList: LiveData<List<Team>> get() = teamListState

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState

    override fun handleEvent(event: TeamEvent) {
        when (event) {
            is TeamEvent.GetTeam -> getTeamList()
            is TeamEvent.OnItemListSwitchCheck -> updateTeam(event.active, event.pos)
        }
    }

    private fun getTeamList() = viewModelScope.launch {
        showLoading()
        when (val result = teamRepo.getTeamList()) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.cannot_read_remote_data)
            })

            is Result.Value -> teamListState.value = result.value.sortedByDescending {
                it.activated
            }
        }
        hideLoading()
    }

    private fun updateTeam(active: Boolean, pos: Int) = viewModelScope.launch {
        showLoading()
        when (val result = teamListState.value?.get(pos)?.uid?.let {
            teamRepo.updateTeamActiveStatus(
                uid = it, active
            )
        }) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.cannot_update_remote_data)
            })

            is Result.Value -> updatedState.value = Unit
            else -> showError(R.string.cannot_read_local_data)
        }
        hideLoading()
    }
}