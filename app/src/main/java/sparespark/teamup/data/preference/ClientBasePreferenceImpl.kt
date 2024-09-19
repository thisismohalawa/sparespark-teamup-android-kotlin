package sparespark.teamup.data.preference

import android.content.Context
import org.threeten.bp.ZonedDateTime
import sparespark.teamup.core.CLIENT_CACHE_TIME

class ClientBasePreferenceImpl(
    context: Context
) : BasePreferenceProvider(context), BaseListPreference {

    private fun inputClientCTime() = sharedPref.getString(CLIENT_CACHE_TIME, "4")?.toInt() ?: 4

    private fun getLastCacheTime() =
        sharedPref.getString(clientLastCache, null)

    override fun updateCacheTimeToNow() =
        prefEditor.putString(clientLastCache, ZonedDateTime.now().toString()).apply()

    override fun clearListCacheTime() =
        prefEditor.remove(clientLastCache).commit()

    override fun isListUpdateNeeded(): Boolean {
        if (getLastCacheTime() == null) return true
        return try {
            val timeAgo = ZonedDateTime.now().minusHours(inputClientCTime().toLong())
            val fetchedTime = ZonedDateTime.parse(getLastCacheTime())
            fetchedTime.isBefore(timeAgo)
        } catch (ex: Exception) {
            true
        }
    }

    override fun isZeroInputCacheTime() = inputClientCTime() == 0
}