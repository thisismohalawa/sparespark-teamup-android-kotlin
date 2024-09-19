package sparespark.teamup.preferences.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import sparespark.teamup.core.wrapper.BaseViewModel
import sparespark.teamup.data.model.UIResource
import sparespark.teamup.data.repository.PreferenceRepository

open class BasePreferenceViewModel<VE>(
    private val preferenceRepo: PreferenceRepository
) : BaseViewModel<VE>() {

    override fun handleEvent(event: VE) = Unit

    private val backupState = MutableLiveData<UIResource>()
    val backup: LiveData<UIResource> get() = backupState

    private val dbClearState = MutableLiveData<Unit>()
    val dbCleared: LiveData<Unit> get() = dbClearState

    protected fun isNoteEnable() = preferenceRepo.getUseNoteStatus()

    protected fun isSuggestAddEnable() = preferenceRepo.getSuggestAddStatus()

    protected fun isSuggestShareEnable() = preferenceRepo.getSuggestShareStatus()

    protected fun isListStaticsEnable() = preferenceRepo.getUseListStaticsStatus()

    protected fun isCalenderStaticsEnable() = preferenceRepo.getUseCalenderStatus()

    protected fun updateBackupTitle(msgRes: Int) {
        backupState.value = UIResource.StringResource(msgRes)
    }

    protected fun dbUpdated() {
        dbClearState.value = Unit
    }
}