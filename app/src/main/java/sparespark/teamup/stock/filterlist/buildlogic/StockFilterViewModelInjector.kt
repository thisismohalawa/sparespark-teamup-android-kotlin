package sparespark.teamup.stock.filterlist.buildlogic

import android.app.Application
import sparespark.teamup.stock.BaseStockInjector

class StockFilterViewModelInjector(
    app: Application
) : BaseStockInjector(app) {
    fun provideViewModelFactory() = StockFilterViewModelFactory(
        getStockRepository(),
        getCityRepository(),
        getClientRepository(),
        getCompanyRepository(),
        getProductRepository(),
        getPreferenceRepository()
    )
}
