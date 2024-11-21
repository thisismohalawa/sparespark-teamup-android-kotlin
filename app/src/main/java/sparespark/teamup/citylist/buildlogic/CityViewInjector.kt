package sparespark.teamup.citylist.buildlogic

import android.app.Application
import sparespark.teamup.core.base.BaseInjector

class CityViewInjector(
    app: Application
) : BaseInjector(app) {
    fun provideViewModelFactory() = CityViewModelFactory(
        getCityRepository()
    )
}