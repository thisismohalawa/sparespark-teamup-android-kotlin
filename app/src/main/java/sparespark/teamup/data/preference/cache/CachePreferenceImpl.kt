package sparespark.teamup.data.preference.cache

import android.content.Context
import sparespark.teamup.data.preference.BasePreferenceCacheProvider

class CachePreferenceImpl(
    context: Context
) : BasePreferenceCacheProvider(context), CachePreference {

    override fun clearLastCacheTimes() = actionClearLastCacheTimes()

    override fun resetInputCacheTimes() = actionResetInputCacheTimes()

}