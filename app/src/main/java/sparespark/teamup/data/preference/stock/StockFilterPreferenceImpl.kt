package sparespark.teamup.data.preference.stock

import android.content.Context
import sparespark.teamup.data.preference.BasePreferenceProvider

private const val STOCK_FILTER_ACTIVE = "STOCK_FILTER_ACTIVE"

class StockFilterPreferenceImpl(
    context: Context
) : BasePreferenceProvider(context), StockFilterPreference {

    override fun isFilterBySellUsed(): Boolean =
        sharedPref.getBoolean(STOCK_FILTER_ACTIVE, false)
}