package sparespark.teamup.data.preference.advance

import android.content.Context
import sparespark.teamup.data.preference.BasePreferenceProvider

private const val SUGGEST_SHARE = "SUGGEST_SHARE"
private const val SUGGEST_ADD = "SUGGEST_ADD"
private const val USE_NOTES = "USE_NOTES"
private const val USE_STATICS = "USE_STATICS"
private const val USE_BALANCE = "USE_BALANCE"

class AdvancedPreferenceImpl(
    context: Context
) : BasePreferenceProvider(context), AdvancedPreference {

    override fun isNotesUsed(): Boolean = sharedPref.getBoolean(USE_NOTES, true)

    override fun isShareActionUsed(): Boolean = sharedPref.getBoolean(SUGGEST_SHARE, false)

    override fun isAddNewActionUsed(): Boolean = sharedPref.getBoolean(SUGGEST_ADD, true)

    override fun getUseStaticsStatus(): Boolean = sharedPref.getBoolean(USE_STATICS, true)

    override fun getUseBalanceStatus(): Boolean = sharedPref.getBoolean(USE_BALANCE, true)
}