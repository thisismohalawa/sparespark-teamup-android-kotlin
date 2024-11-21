package sparespark.teamup.data.preference.util

import android.content.Context
import sparespark.teamup.core.USE_BACKUP
import sparespark.teamup.core.USE_REMOTE_SERVER
import sparespark.teamup.data.preference.BasePreferenceProvider

class UtilPreferenceImpl(
    context: Context,
) : BasePreferenceProvider(context), UtilPreference {

    override fun isRemoteServerUsed(): Boolean = sharedPref.getBoolean(USE_REMOTE_SERVER, false)

    override fun isAutoBackupUsed(): Boolean = sharedPref.getBoolean(USE_BACKUP, false)

    override fun updateRemoteServerUse(enable: Boolean) =
        prefEditor.putBoolean(USE_REMOTE_SERVER, enable).apply()

    override fun updateAutoBackupUse(enable: Boolean) =
        prefEditor.putBoolean(USE_BACKUP, enable).apply()
}