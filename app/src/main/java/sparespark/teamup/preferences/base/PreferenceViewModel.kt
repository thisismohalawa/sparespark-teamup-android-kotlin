package sparespark.teamup.preferences.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.remider.ReminderAPI
import sparespark.teamup.data.repository.PreferenceRepository

class PreferenceViewModel(
    private val prefRepo: PreferenceRepository,
    private val reminderAPI: ReminderAPI
) : BasePreferenceViewModel<PreferenceEvent>(prefRepo) {

    private val cUpdateState = MutableLiveData<Unit>()
    val cacheUpdated: LiveData<Unit> get() = cUpdateState

    override fun handleEvent(event: PreferenceEvent) {
        when (event) {
            is PreferenceEvent.OnResetCacheClick -> {
                resetInputCacheTimes()
            }

            is PreferenceEvent.OnClearCacheClick -> {
                clearPrefLastCacheTimes()
                clearTeamDatabase()
            }

            is PreferenceEvent.OnServerSwitchUpdate -> {
                updateRemoteServerUse(event.enable)
                if (event.enable) clearPrefLastCacheTimes()
            }

            is PreferenceEvent.OnBackupSwitchUpdate -> {
                updateAutoBackupUse(event.enable)
                updateBackupReminder(event.enable)
            }
        }
    }

    private fun resetInputCacheTimes() = viewModelScope.launch {
        if (prefRepo.resetPrefInputCacheTimes() is Result.Value) cUpdateState.value = Unit
        else showError(R.string.cannot_update_local_entries)
    }

    private fun clearPrefLastCacheTimes() = viewModelScope.launch {
        if (prefRepo.clearPrefLastCacheTimes() is Result.Value) cUpdateState.value = Unit
        else showError(R.string.cannot_update_local_entries)
    }

    private fun clearTeamDatabase() = viewModelScope.launch {
        if (prefRepo.clearDatabase() is Result.Value) dbUpdated()
        else showError(R.string.cannot_update_local_entries)
    }

    private fun updateRemoteServerUse(isEnable: Boolean) = viewModelScope.launch {
        if (prefRepo.updatePrefRemoteServerUse(isEnable) is Result.Error)
            showError(R.string.cannot_update_local_entries)
    }


    private fun updateAutoBackupUse(enable: Boolean) = viewModelScope.launch {
        if (prefRepo.updatePrefAutoBackupUse(enable) is Result.Error)
            showError(R.string.cannot_update_local_entries)
    }

    private fun updateBackupReminder(enable: Boolean) {
        when (enable) {
            true -> if (reminderAPI.setupReminderAlarmForBackup() is Result.Value)
                updateBackupTitle(R.string.backup_setup)
            else showError(R.string.error_set_backup_alarm)

            false -> if (reminderAPI.cancelBackupReminder() is Result.Value)
                updateBackupTitle(R.string.backup_canceled)
            else showError(R.string.error_cancel_backup_alarm)
        }
    }
}