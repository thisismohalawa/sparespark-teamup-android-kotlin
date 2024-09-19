package sparespark.teamup.auh

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import sparespark.teamup.data.implementation.LoginRepositoryImpl
import sparespark.teamup.home.base.BaseInjector

open class BaseAuthInjector(
    app: Application
) : BaseInjector(app) {

    protected fun getLoginRepository() = LoginRepositoryImpl(
        FirebaseAuth.getInstance(), getUserDao(), getUtilListPref(), getConnectInterceptor()
    )
}
