package sparespark.teamup.data.preference.transaction

import android.content.Context
import sparespark.teamup.data.preference.BasePreferenceProvider

private const val TRANS_FILTER_ACTIVE = "TRANS_FILTER_ACTIVE"
private const val TRANS_FILTER_SELL = "TRANS_FILTER_SELL"
private const val TRANS_FILTER_THIS_MONTH = "TRANS_FILTER_THIS_MONTH"

class TransactionFilterPreferenceImpl(
    context: Context
) : BasePreferenceProvider(context), TransactionFilterPreference {

    override fun isFilterThisMonthUsed(): Boolean =
        sharedPref.getBoolean(TRANS_FILTER_THIS_MONTH, true)

    override fun isFilterByActiveUsed(): Boolean =
        sharedPref.getBoolean(TRANS_FILTER_ACTIVE, false)

    override fun isFilterBySellUsed(): Boolean =
        sharedPref.getBoolean(TRANS_FILTER_SELL, false)

}