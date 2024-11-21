package sparespark.teamup.data.preference

import android.content.Context
import org.threeten.bp.ZonedDateTime
import sparespark.teamup.core.COMPANY_CACHE_TIME

class CompanyPreferenceImpl(
    context: Context,
) : BasePreferenceCacheProvider(context), BaseListPreference {

    private fun inputCompanyCTime() = sharedPref.getString(COMPANY_CACHE_TIME, "4")?.toInt() ?: 4

    private fun getLastCacheTime() = sharedPref.getString(companyLastCache, null)

    override fun updateCacheTimeToNow() =
        prefEditor.putString(companyLastCache, ZonedDateTime.now().toString()).apply()

    override fun clearListCacheTime() = prefEditor.remove(companyLastCache).commit()

    override fun isListUpdateNeeded(): Boolean {
        if (getLastCacheTime() == null) return true
        return try {
            val timeAgo = ZonedDateTime.now().minusHours(inputCompanyCTime().toLong())
            val fetchedTime = ZonedDateTime.parse(getLastCacheTime())
            fetchedTime.isBefore(timeAgo)
        } catch (ex: Exception) {
            true
        }
    }

    override fun isZeroInputCacheTime() = inputCompanyCTime() == 0
}