package sparespark.teamup.data.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

open class BasePreferenceProvider(context: Context) {
    private val appContext = context.applicationContext

    protected val sharedPref: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    protected val prefEditor: SharedPreferences.Editor = sharedPref.edit()

}