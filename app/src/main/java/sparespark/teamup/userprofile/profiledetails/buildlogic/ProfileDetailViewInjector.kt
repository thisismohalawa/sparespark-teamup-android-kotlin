package sparespark.teamup.userprofile.profiledetails.buildlogic

import android.app.Application
import sparespark.teamup.home.base.BaseInjector

class ProfileDetailViewInjector(
    app: Application
) : BaseInjector(app) {

    fun provideViewModelFactory() = ProfileDetailViewModelFactory(userRepo = getUserRepository())
}
