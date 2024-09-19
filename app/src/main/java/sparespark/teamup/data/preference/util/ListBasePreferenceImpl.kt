package sparespark.teamup.data.preference.util

import android.content.Context
import sparespark.teamup.core.USE_BACKUP
import sparespark.teamup.core.USE_REMOTE_SERVER
import sparespark.teamup.data.preference.BasePreferenceProvider

private const val FILTER_BY_MONTH_ONLY = "FILTER_BY_MONTH_ONLY"

class ListBasePreferenceImpl(
    context: Context
) : BasePreferenceProvider(context), ListPreference {

    override fun isRemoteServerUsed() = sharedPref.getBoolean(USE_REMOTE_SERVER, false)

    override fun isAutoBackupUsed() = sharedPref.getBoolean(USE_BACKUP, false)

    override fun isFilterThisMonthUsed(): Boolean =
        sharedPref.getBoolean(FILTER_BY_MONTH_ONLY, true)

    override fun updateRemoteServerUse(enable: Boolean) =
        prefEditor.putBoolean(USE_REMOTE_SERVER, enable).apply()

    override fun updateAutoBackupUse(enable: Boolean) =
        prefEditor.putBoolean(USE_BACKUP, enable).apply()
}