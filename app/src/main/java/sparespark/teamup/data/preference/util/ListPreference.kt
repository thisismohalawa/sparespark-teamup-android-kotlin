package sparespark.teamup.data.preference.util

interface ListPreference {
    fun isRemoteServerUsed(): Boolean
    fun isAutoBackupUsed(): Boolean
    fun isFilterThisMonthUsed(): Boolean
    fun updateRemoteServerUse(enable: Boolean)
    fun updateAutoBackupUse(enable: Boolean)
}