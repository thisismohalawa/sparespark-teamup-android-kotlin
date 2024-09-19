package sparespark.teamup.data.preference

import android.content.Context
import org.threeten.bp.ZonedDateTime
import sparespark.teamup.core.CITY_CACHE_TIME

class CityBasePreferenceImpl(
    context: Context,
) : BasePreferenceProvider(context), BaseListPreference {

    private fun inputCityCTime() = sharedPref.getString(CITY_CACHE_TIME, "4")?.toInt() ?: 4

    private fun getLastCacheTime() = sharedPref.getString(cityLastCache, null)

    override fun updateCacheTimeToNow() =
        prefEditor.putString(cityLastCache, ZonedDateTime.now().toString()).apply()

    override fun clearListCacheTime() = prefEditor.remove(cityLastCache).commit()

    override fun isListUpdateNeeded(): Boolean {
        if (getLastCacheTime() == null) return true
        return try {
            val timeAgo = ZonedDateTime.now().minusHours(inputCityCTime().toLong())
            val fetchedTime = ZonedDateTime.parse(getLastCacheTime())
            fetchedTime.isBefore(timeAgo)
        } catch (ex: Exception) {
            true
        }
    }

    override fun isZeroInputCacheTime() = inputCityCTime() == 0
}