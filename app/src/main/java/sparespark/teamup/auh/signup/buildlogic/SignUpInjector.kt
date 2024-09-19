package sparespark.teamup.auh.signup.buildlogic

import android.app.Application
import sparespark.teamup.auh.BaseAuthInjector

class SignUpInjector(
    app: Application
) : BaseAuthInjector(app) {

    fun provideViewModelFactory() = SignUpViewModelFactory(
        getLoginRepository()
    )
}
