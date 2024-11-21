package sparespark.teamup.data.preference.util

interface UtilPreference {
    fun isRemoteServerUsed(): Boolean
    fun isAutoBackupUsed(): Boolean
    fun updateRemoteServerUse(enable: Boolean)
    fun updateAutoBackupUse(enable: Boolean)
}