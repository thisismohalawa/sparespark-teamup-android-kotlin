package sparespark.teamup.auth.signup.buildlogic

import android.app.Application
import sparespark.teamup.auth.BaseAuthInjector

class SignUpInjector(
    app: Application
) : BaseAuthInjector(app) {

    fun provideViewModelFactory() = SignUpViewModelFactory(
        getLoginRepository()
    )
}
