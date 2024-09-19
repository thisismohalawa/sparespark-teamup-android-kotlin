package sparespark.teamup.preferences.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.data.remider.ReminderAPI
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.preferences.base.PreferenceViewModel

class PreferenceViewModelFactory(
    private val preferenceRepo: PreferenceRepository,
    private val reminderAPI: ReminderAPI
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(PreferenceViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            PreferenceViewModel(preferenceRepo, reminderAPI) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
