package sparespark.teamup.companylist.buildlogic

import android.app.Application
import sparespark.teamup.core.base.BaseInjector

class CompanyViewInjector(
    app: Application
) : BaseInjector(app) {
    fun provideViewModelFactory() = CompanyViewModelFactory(
        getCompanyRepository()
    )
}