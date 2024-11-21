package sparespark.teamup.stock.itemsell.buildllogic

import android.app.Application
import sparespark.teamup.stock.BaseStockInjector

class StockSellViewInjector(
    app: Application
) : BaseStockInjector(app) {
    fun provideViewModelFactory() = StockSellViewModelFactory(
        getStockRepository(),
        getClientRepository(),
        getPreferenceRepository()
    )
}
