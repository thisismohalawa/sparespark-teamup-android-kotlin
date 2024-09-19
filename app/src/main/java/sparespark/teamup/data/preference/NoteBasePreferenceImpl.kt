package sparespark.teamup.data.preference

import android.content.Context
import org.threeten.bp.ZonedDateTime
import sparespark.teamup.core.NOTE_CACHE_TIME

class NoteBasePreferenceImpl(
    context: Context
) : BasePreferenceProvider(context), BaseListPreference {

    private fun inputNotesCTime() = sharedPref.getString(NOTE_CACHE_TIME, "10")?.toInt() ?: 10

    private fun getLastCacheTime() = sharedPref.getString(noteLastCache, null)

    override fun clearListCacheTime() = prefEditor.remove(noteLastCache).commit()

    override fun updateCacheTimeToNow() =
        prefEditor.putString(noteLastCache, ZonedDateTime.now().toString()).apply()

    override fun isListUpdateNeeded(): Boolean {
        if (getLastCacheTime() == null) return true
        return try {
            val timeAgo = ZonedDateTime.now().minusMinutes(inputNotesCTime().toLong())
            val fetchedTime = ZonedDateTime.parse(getLastCacheTime())
            fetchedTime.isBefore(timeAgo)
        } catch (ex: Exception) {
            true
        }
    }

    override fun isZeroInputCacheTime() = inputNotesCTime() == 0
}