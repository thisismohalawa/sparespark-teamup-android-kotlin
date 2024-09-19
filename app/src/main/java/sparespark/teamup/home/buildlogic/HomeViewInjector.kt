package sparespark.teamup.home.buildlogic

import android.app.Application
import sparespark.teamup.home.base.BaseInjector

class HomeViewInjector(
    app: Application
) : BaseInjector(app) {
    fun provideViewModelFactory() = HomeViewModelFactory(getUserRepository())
}
