package sparespark.teamup.data.preference.transaction

interface TransactionBalancePreference {
    fun isBalanceTodayOnly(): Boolean
    fun isBalanceActiveOnly(): Boolean
}