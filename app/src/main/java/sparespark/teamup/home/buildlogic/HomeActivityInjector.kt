package sparespark.teamup.home.buildlogic

import android.app.Application
import sparespark.teamup.core.base.BaseInjector

class HomeActivityInjector(
    app: Application
) : BaseInjector(app) {
    fun provideViewModelFactory() = HomeActivityViewModelFactory(getUserRepository())
}
