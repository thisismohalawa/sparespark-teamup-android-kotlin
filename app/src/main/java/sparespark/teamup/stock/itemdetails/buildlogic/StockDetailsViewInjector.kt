package sparespark.teamup.stock.itemdetails.buildlogic

import android.app.Application
import sparespark.teamup.stock.BaseStockInjector

class StockDetailsViewInjector(
    app: Application
) : BaseStockInjector(app) {
    fun provideViewModelFactory() = StockDetailsViewModelFactory(
        getStockRepository(),
        getProductRepository(),
        getPreferenceRepository()
    )
}
