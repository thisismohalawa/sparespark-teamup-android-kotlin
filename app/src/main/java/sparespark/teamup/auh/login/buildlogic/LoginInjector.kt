package sparespark.teamup.auh.login.buildlogic

import android.app.Application
import sparespark.teamup.auh.BaseAuthInjector

class LoginInjector(
    app: Application
) : BaseAuthInjector(app) {

    fun provideViewModelFactory() = LoginViewModelFactory(
        getLoginRepository(), getUserRepository()
    )
}
