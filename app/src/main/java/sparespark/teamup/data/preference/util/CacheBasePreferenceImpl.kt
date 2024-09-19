package sparespark.teamup.data.preference.util

import android.content.Context
import sparespark.teamup.data.preference.BasePreferenceProvider

class CacheBasePreferenceImpl(
    context: Context
) : BasePreferenceProvider(context), CachePreference {

    override fun clearLastCacheTimes() = baseClearLastCacheTimes()

    override fun resetInputCacheTimes() = baseResetInputCacheTimes()

}