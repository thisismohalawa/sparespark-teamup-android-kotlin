package sparespark.teamup.preferences

sealed class PreferenceEvent {
    data object OnClearCacheClick : PreferenceEvent()
    data object OnResetCacheClick : PreferenceEvent()
    data class OnServerSwitchUpdate(val enable: Boolean) : PreferenceEvent()
    data class OnBackupSwitchUpdate(val enable: Boolean) : PreferenceEvent()
}