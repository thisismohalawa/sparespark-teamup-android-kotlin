package sparespark.teamup.data.preference.transaction

import android.content.Context
import sparespark.teamup.data.preference.BasePreferenceProvider

private const val TRANS_BALANCE_TODAY_ONLY = "TRANS_BALANCE_TODAY_ONLY"
private const val TRANS_BALANCE_ACTIVE_ONLY = "TRANS_BALANCE_ACTIVE_ONLY"

class TransactionBalancePreferenceImpl(
    context: Context
) : BasePreferenceProvider(context), TransactionBalancePreference {

    override fun isBalanceTodayOnly(): Boolean =
        sharedPref.getBoolean(TRANS_BALANCE_TODAY_ONLY, true)

    override fun isBalanceActiveOnly(): Boolean =
        sharedPref.getBoolean(TRANS_BALANCE_ACTIVE_ONLY, false)
}