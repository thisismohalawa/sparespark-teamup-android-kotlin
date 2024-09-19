package sparespark.teamup.data.preference

interface BaseListPreference {

    fun updateCacheTimeToNow()

    fun clearListCacheTime(): Boolean

    fun isListUpdateNeeded(): Boolean

    fun isZeroInputCacheTime(): Boolean
}
