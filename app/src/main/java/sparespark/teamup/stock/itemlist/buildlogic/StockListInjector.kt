package sparespark.teamup.stock.itemlist.buildlogic

import android.app.Application
import sparespark.teamup.stock.BaseStockInjector

class StockListInjector(
    app: Application
) : BaseStockInjector(app) {
    fun provideViewModelFactory() = StockListViewModelFactory(
        getStockRepository(),
        getPreferenceRepository()
    )
}
