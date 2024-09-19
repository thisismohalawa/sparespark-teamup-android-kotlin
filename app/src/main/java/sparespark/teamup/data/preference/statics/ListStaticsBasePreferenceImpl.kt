package sparespark.teamup.data.preference.statics

import android.content.Context
import sparespark.teamup.core.DATE_SEARCH_FORMAT
import sparespark.teamup.core.USE_STATICS_ACTIVE_ONLY
import sparespark.teamup.core.USE_STATICS_TODAY_ONLY
import sparespark.teamup.core.getCalendarDateTime
import sparespark.teamup.data.model.statics.CStatics
import sparespark.teamup.data.preference.BasePreferenceProvider

class ListStaticsBasePreferenceImpl(
    context: Context
) : BasePreferenceProvider(context), ListStaticsPreference {

    override fun isLStaticsTodayOnlyUsed() = sharedPref.getBoolean(USE_STATICS_TODAY_ONLY, true)

    override fun isLStaticsActiveOnlyUsed(): Boolean =
        sharedPref.getBoolean(USE_STATICS_ACTIVE_ONLY, true)
}