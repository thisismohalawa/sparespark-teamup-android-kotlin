package sparespark.teamup.data.preference.util

import android.content.Context
import sparespark.teamup.data.preference.BasePreferenceProvider

private const val USE_NOTES = "USE_NOTES"
private const val USE_CALENDER = "USE_CALENDER"
private const val USE_STATICS = "USE_STATICS"
private const val SUGGEST_SHARE = "SUGGEST_SHARE"
private const val SUGGEST_ADD = "SUGGEST_ADD"

class AdvanceBasePreferenceImpl(
    context: Context
) : BasePreferenceProvider(context), AdvancePreference {

    override fun isNotesUsed() = sharedPref.getBoolean(USE_NOTES, true)

    override fun isCalenderStaticsUsed() = sharedPref.getBoolean(USE_CALENDER, true)

    override fun isListStaticsUsed() = sharedPref.getBoolean(USE_STATICS, true)

    override fun isShareActionUsed() = sharedPref.getBoolean(SUGGEST_SHARE, false)

    override fun isAddNewActionUsed() = sharedPref.getBoolean(SUGGEST_ADD, true)
}