package sparespark.teamup.data.preference.transaction

interface TransactionFilterPreference {
    fun isFilterThisMonthUsed(): Boolean
    fun isFilterByActiveUsed(): Boolean
    fun isFilterBySellUsed(): Boolean
}