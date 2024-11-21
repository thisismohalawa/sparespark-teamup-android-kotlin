package sparespark.teamup.productlist.buildlogic

import android.app.Application
import sparespark.teamup.core.base.BaseInjector

class ProductViewInjector(
    app: Application
) : BaseInjector(app) {
    fun provideViewModelFactory() = ProductViewModelFactory(
        getProductRepository(),
        getCompanyRepository(),
        getPreferenceRepository()
    )
}