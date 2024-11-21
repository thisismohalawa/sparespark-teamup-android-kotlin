package sparespark.teamup.preferences.buildlogic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.base.BaseViewModel
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.core.wrapper.UIResource
import sparespark.teamup.data.reminder.ReminderAPI
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.preferences.PreferenceEvent

class PreferenceViewModel(
    private val preferenceRepo: PreferenceRepository,
    private val reminderAPI: ReminderAPI
) : BaseViewModel<PreferenceEvent>() {

    private val cacheUpdateState = MutableLiveData<Unit>()
    val cacheUpdated: LiveData<Unit> get() = cacheUpdateState

    private val backupTextState = MutableLiveData<UIResource>()
    val backupText: LiveData<UIResource> get() = backupTextState

    private val dbClearState = MutableLiveData<Unit>()
    val dbCleared: LiveData<Unit> get() = dbClearState

    override fun handleEvent(event: PreferenceEvent) {
        when (event) {
            PreferenceEvent.OnClearCacheClick -> {
                clearPrefLastCacheTimes()
                clearTeamDatabase()
            }

            PreferenceEvent.OnResetCacheClick -> {
                resetInputCacheTimes()
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
        if (preferenceRepo.resetInputCacheTimes() is Result.Value) cacheUpdateState.value = Unit
        else showError(R.string.cannot_update_local_entries)
    }

    private fun clearPrefLastCacheTimes() = viewModelScope.launch {
        if (preferenceRepo.clearLastCacheTimes() is Result.Value) cacheUpdateState.value = Unit
        else showError(R.string.cannot_update_local_entries)
    }

    private fun clearTeamDatabase() = viewModelScope.launch {
        if (preferenceRepo.clearAppDatabase() is Result.Value) dbClearState.value = Unit
        else showError(R.string.cannot_update_local_entries)
    }

    private fun updateRemoteServerUse(isEnable: Boolean) = viewModelScope.launch {
        if (preferenceRepo.updateRemoteServerUse(isEnable) is Result.Error)
            showError(R.string.cannot_update_local_entries)
    }

    private fun updateAutoBackupUse(enable: Boolean) = viewModelScope.launch {
        if (preferenceRepo.updateAutoBackupUse(enable) is Result.Error)
            showError(R.string.cannot_update_local_entries)
    }


    private fun updateBackupTitle(msgRes: Int) {
        backupTextState.value = UIResource.StringResource(msgRes)
    }

    private fun updateBackupReminder(enable: Boolean) {
        when (enable) {
            true -> if (reminderAPI.setupReminderAlarmForBackup() is Result.Value)
                updateBackupTitle(R.string.backup_setup)
            else showError(R.string.error_set_backup_alarm)

            false -> if (reminderAPI.cancelBackupReminder() is Result.Value)
                updateBackupTitle(R.string.backup_canceled)
            else showError(R.string.error_set_backup_alarm)
        }
    }

}