package sparespark.teamup.data.preference

import android.content.Context
import org.threeten.bp.ZonedDateTime
import sparespark.teamup.core.ITEM_CACHE_TIME

class ItemBasePreferenceImpl(
    context: Context
) : BasePreferenceProvider(context), BaseListPreference {

    private fun inputItemsCTime() = sharedPref.getString(ITEM_CACHE_TIME, "10")?.toInt() ?: 10

    private fun getLastCacheTime() = sharedPref.getString(itemLastCache, null)

    override fun clearListCacheTime() = prefEditor.remove(itemLastCache).commit()

    override fun updateCacheTimeToNow() =
        prefEditor.putString(itemLastCache, ZonedDateTime.now().toString()).apply()

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
