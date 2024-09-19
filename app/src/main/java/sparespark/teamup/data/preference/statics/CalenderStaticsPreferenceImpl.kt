package sparespark.teamup.data.preference.statics

import android.content.Context
import sparespark.teamup.core.DATE_SEARCH_FORMAT
import sparespark.teamup.core.getCalendarDateTime
import sparespark.teamup.data.model.statics.CStatics
import sparespark.teamup.data.preference.BasePreferenceProvider


private const val DEF_TOTAL = 0
private const val total_yesterday = "total_yesterday"
private const val total_uncompleted = "total_uncompleted"
private const val total_today = "total_today"

class CalenderStaticsPreferenceImpl(
    context: Context
) : BasePreferenceProvider(context), CalenderStaticsPreference {

    private fun getLastCalenderCacheTime() = sharedPref.getString(cStaticsLastCache, null)

    override fun isListUpdateNeeded(): Boolean = getLastCalenderCacheTime() == null ||
            getLastCalenderCacheTime() != getCalendarDateTime(DATE_SEARCH_FORMAT)

    override fun updateCacheTimeToNow() =
        prefEditor.putString(cStaticsLastCache, getCalendarDateTime(DATE_SEARCH_FORMAT))
            .apply()

    override fun clearListCacheTime(): Boolean = prefEditor.remove(cStaticsLastCache).commit()

    override fun isZeroInputCacheTime(): Boolean = false

    override fun getCalenderStatics(): CStatics = CStatics(
        totalYesterday = sharedPref.getInt(total_yesterday, DEF_TOTAL),
        totalUnCompleted = sharedPref.getInt(total_uncompleted, DEF_TOTAL),
        totalToday = sharedPref.getInt(total_today, DEF_TOTAL)
    )

    override fun updateCalenderStatics(statics: CStatics) {
        prefEditor.apply {
            putInt(total_yesterday, statics.totalYesterday)
            putInt(total_uncompleted, statics.totalUnCompleted)
            putInt(total_today, statics.totalToday)
            apply()
        }
    }
}