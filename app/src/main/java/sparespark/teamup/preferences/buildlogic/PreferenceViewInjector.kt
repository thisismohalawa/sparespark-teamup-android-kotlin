package sparespark.teamup.preferences.buildlogic

import android.app.Application
import sparespark.teamup.core.base.BaseInjector
import sparespark.teamup.data.reminder.ReminderAPIImpl

class PreferenceViewInjector(
    app: Application
) : BaseInjector(app) {
    fun provideViewModelFactory() = PreferenceViewModelFactory(
        preferenceRepo = getPreferenceRepository(),
        reminderAPI = ReminderAPIImpl(getApplication())
    )
}
