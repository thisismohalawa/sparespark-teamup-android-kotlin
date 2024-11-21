package sparespark.teamup.clientlist.buildlogic

import android.app.Application
import sparespark.teamup.core.base.BaseInjector

class ClientViewInjector(
    app: Application
) : BaseInjector(app) {

    fun provideViewModelFactory() = ClientViewModelFactory(
        getClientRepository(),
        getCityRepository(),
        getPreferenceRepository()
    )
}