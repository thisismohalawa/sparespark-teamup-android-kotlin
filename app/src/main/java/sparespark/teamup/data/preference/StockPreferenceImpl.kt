package sparespark.teamup.data.preference

import android.content.Context
import org.threeten.bp.ZonedDateTime
import sparespark.teamup.core.STOCK_CACHE_TIME

class StockPreferenceImpl(
    context: Context
) : BasePreferenceCacheProvider(context), BaseListPreference {

    private fun inputItemsCTime() =
        sharedPref.getString(STOCK_CACHE_TIME, "10")?.toInt() ?: 10

    private fun getLastCacheTime() = sharedPref.getString(stockLastCache, null)

    override fun clearListCacheTime() = prefEditor.remove(stockLastCache).commit()

    override fun updateCacheTimeToNow() =
        prefEditor.putString(stockLastCache, ZonedDateTime.now().toString()).apply()

    override fun isListUpdateNeeded(): Boolean {
        if (getLastCacheTime() == null) return true
        return try {
            val timeAgo = ZonedDateTime.now().minusMinutes(inputItemsCTime().toLong())
            val fetchedTime = ZonedDateTime.parse(getLastCacheTime())
            fetchedTime.isBefore(timeAgo)
        } catch (ex: Exception) {
            true
        }
    }

    override fun isZeroInputCacheTime() = inputItemsCTime() == 0
}
