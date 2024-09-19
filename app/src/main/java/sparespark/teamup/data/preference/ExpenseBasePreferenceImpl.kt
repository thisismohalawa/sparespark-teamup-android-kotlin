package sparespark.teamup.data.preference

import android.content.Context
import org.threeten.bp.ZonedDateTime
import sparespark.teamup.core.EXPENSE_CACHE_TIME

class ExpenseBasePreferenceImpl(
    context: Context
) : BasePreferenceProvider(context), BaseListPreference {

    private fun inputExpensesCTime() = sharedPref.getString(EXPENSE_CACHE_TIME, "1")?.toInt() ?: 1

    private fun getLastCacheTime() =
        sharedPref.getString(expenseLastCache, null)

    override fun updateCacheTimeToNow() =
        prefEditor.putString(expenseLastCache, ZonedDateTime.now().toString()).apply()

    override fun clearListCacheTime() =
        prefEditor.remove(expenseLastCache).commit()

    override fun isListUpdateNeeded(): Boolean {
        if (getLastCacheTime() == null) return true
        return try {
            val timeAgo = ZonedDateTime.now().minusHours(inputExpensesCTime().toLong())
            val fetchedTime = ZonedDateTime.parse(getLastCacheTime())
            fetchedTime.isBefore(timeAgo)
        } catch (ex: Exception) {
            true
        }
    }

    override fun isZeroInputCacheTime() = inputExpensesCTime() == 0
}