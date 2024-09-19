package sparespark.teamup.data.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import sparespark.teamup.core.CITY_CACHE_TIME
import sparespark.teamup.core.CLIENT_CACHE_TIME
import sparespark.teamup.core.EXPENSE_CACHE_TIME
import sparespark.teamup.core.ITEM_CACHE_TIME
import sparespark.teamup.core.NOTE_CACHE_TIME

abstract class BasePreferenceProvider(context: Context) {
    private val appContext = context.applicationContext

    protected val sharedPref: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    protected val prefEditor: SharedPreferences.Editor = sharedPref.edit()

    protected val cityLastCache = "city_last_cache"
    protected val clientLastCache = "client_last_cache"
    protected val expenseLastCache = "expense_last_cache"
    protected val itemLastCache = "item_last_cache"
    protected val noteLastCache = "note_last_cache"
    protected val cStaticsLastCache = "calender_statics_last_cache"


    protected fun baseClearLastCacheTimes() {
        if (sharedPref.getString(itemLastCache, null) != null) {
            prefEditor.let {
                it.putString(cityLastCache, null)
                it.putString(clientLastCache, null)
                it.putString(itemLastCache, null)
                it.putString(noteLastCache, null)
                it.putString(expenseLastCache, null)
                it.putString(cStaticsLastCache, null)
                it.apply()
            }
        }
    }

    protected fun baseResetInputCacheTimes() {
        prefEditor.let {
            it.putString(CITY_CACHE_TIME, "4")
            it.putString(CLIENT_CACHE_TIME, "4")
            it.putString(EXPENSE_CACHE_TIME, "1")
            it.putString(NOTE_CACHE_TIME, "10")
            it.putString(ITEM_CACHE_TIME, "10")
            it.apply()
        }
    }
}
