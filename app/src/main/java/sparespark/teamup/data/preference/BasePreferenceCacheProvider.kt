package sparespark.teamup.data.preference

import android.content.Context
import sparespark.teamup.core.CITY_CACHE_TIME
import sparespark.teamup.core.CLIENT_CACHE_TIME
import sparespark.teamup.core.COMPANY_CACHE_TIME
import sparespark.teamup.core.NOTE_CACHE_TIME
import sparespark.teamup.core.PRODUCT_CACHE_TIME
import sparespark.teamup.core.STOCK_CACHE_TIME
import sparespark.teamup.core.TRANSACTION_CACHE_TIME

open class BasePreferenceCacheProvider(context: Context) :
    BasePreferenceProvider(context) {

    protected val cityLastCache = "city_last_cache"
    protected val clientLastCache = "client_last_cache"
    protected val transactionLastCache = "transaction_last_cache"
    protected val noteLastCache = "note_last_cache"
    protected val stockLastCache = "stock_last_cache"
    protected val productLastCache = "product_last_cache"
    protected val companyLastCache = "company_last_cache"

    protected fun actionClearLastCacheTimes() {
        if (sharedPref.getString(transactionLastCache, null) != null) {
            prefEditor.let {
                it.putString(cityLastCache, null)
                it.putString(clientLastCache, null)
                it.putString(transactionLastCache, null)
                it.putString(productLastCache, null)
                it.putString(companyLastCache, null)
                it.putString(noteLastCache, null)
                it.putString(stockLastCache, null)
                it.apply()
            }
        }
    }

    protected fun actionResetInputCacheTimes() {
        prefEditor.let {
            it.putString(CITY_CACHE_TIME, "4")
            it.putString(CLIENT_CACHE_TIME, "4")
            it.putString(PRODUCT_CACHE_TIME, "4")
            it.putString(COMPANY_CACHE_TIME, "4")
            it.putString(NOTE_CACHE_TIME, "10")
            it.putString(TRANSACTION_CACHE_TIME, "10")
            it.putString(STOCK_CACHE_TIME, "10")
            it.apply()
        }
    }
}