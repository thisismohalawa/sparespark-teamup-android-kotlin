package sparespark.teamup.stock

import android.app.Application
import sparespark.teamup.core.base.BaseInjector
import sparespark.teamup.data.implementation.StockRepositoryImpl
import sparespark.teamup.data.preference.StockPreferenceImpl
import sparespark.teamup.data.preference.stock.StockFilterPreferenceImpl
import sparespark.teamup.data.room.TeamDatabase

open class BaseStockInjector(
    app: Application
) : BaseInjector(app) {

    private fun getStockDao() = TeamDatabase.invoke(getApplication()).stockDao()

    private fun getStockPreference() = StockPreferenceImpl(getApplication())

    private fun getStockFilterPreference() = StockFilterPreferenceImpl(getApplication())

    protected fun getStockRepository() = StockRepositoryImpl(
        local = getStockDao(),
        pref = getStockPreference(),
        filterPreference = getStockFilterPreference(),
        localUser = getUserDao(),
        utilPreference = getUtilPreference(),
        connectInterceptor = getConnectInterceptor()
    )
}