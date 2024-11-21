package sparespark.teamup.auth

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import sparespark.teamup.core.base.BaseInjector
import sparespark.teamup.data.implementation.LoginRepositoryImpl

open class BaseAuthInjector(
    app: Application
) : BaseInjector(app) {

    protected fun getLoginRepository() = LoginRepositoryImpl(
        FirebaseAuth.getInstance()
    )
}
