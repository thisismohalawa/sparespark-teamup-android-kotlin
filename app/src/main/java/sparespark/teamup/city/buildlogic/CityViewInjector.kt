package sparespark.teamup.city.buildlogic

import android.app.Application
import sparespark.teamup.home.base.BaseInjector

class CityViewInjector(
    app: Application
) : BaseInjector(app) {
    fun provideViewModelFactory() = CityViewModelFactory(getCityRepository())
}
