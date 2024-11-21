package sparespark.teamup.auth.login.buildlogic

import android.app.Application
import sparespark.teamup.auth.BaseAuthInjector

class LoginInjector(
    app: Application
) : BaseAuthInjector(app) {

    fun provideViewModelFactory() = LoginViewModelFactory(
        getLoginRepository(), getUserRepository()
    )
}
