package sparespark.teamup.client.buildlogic

import android.app.Application
import sparespark.teamup.home.base.BaseInjector

class ClientViewInjector(
    app: Application
) : BaseInjector(app) {

    fun provideViewModelFactory() = ClientViewModelFactory(
        getClientRepository(), getCityRepository(), getPreferenceRepository()
    )
}
