package sparespark.teamup.userprofile.profile.buildlogic

import android.app.Application
import sparespark.teamup.home.base.BaseInjector

class ProfileViewInjector(
    app: Application
) : BaseInjector(app) {

    fun provideViewModelFactory() = ProfileViewModelFactory(userRepo = getUserRepository())
}
