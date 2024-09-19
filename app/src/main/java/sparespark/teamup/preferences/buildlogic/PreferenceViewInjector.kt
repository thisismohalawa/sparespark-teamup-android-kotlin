package sparespark.teamup.preferences.buildlogic

import android.app.Application
import sparespark.teamup.data.remider.ReminderAPIImpl
import sparespark.teamup.home.base.BaseInjector

class PreferenceViewInjector(
    app: Application
) : BaseInjector(app) {

    fun provideViewModelFactory() =
        PreferenceViewModelFactory(getPreferenceRepository(), ReminderAPIImpl(getApplication()))
}
