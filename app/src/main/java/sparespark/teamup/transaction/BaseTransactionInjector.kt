package sparespark.teamup.transaction

import android.app.Application
import sparespark.teamup.core.base.BaseInjector
import sparespark.teamup.data.implementation.TransactionRepositoryImpl
import sparespark.teamup.data.preference.TransactionPreferenceImpl
import sparespark.teamup.data.preference.transaction.TransactionBalancePreferenceImpl
import sparespark.teamup.data.preference.transaction.TransactionFilterPreferenceImpl
import sparespark.teamup.data.room.TeamDatabase


open class BaseTransactionInjector(
    app: Application
) : BaseInjector(app) {

    private fun getTransactionDao() = TeamDatabase.invoke(getApplication()).transactionDao()

    private fun getTransactionPreference() = TransactionPreferenceImpl(getApplication())

    private fun getTransactionFilterPreference() = TransactionFilterPreferenceImpl(getApplication())

    private fun getTransactionBalancePreference() =
        TransactionBalancePreferenceImpl(getApplication())

    protected fun getTransactionRepository() = TransactionRepositoryImpl(
        local = getTransactionDao(),
        pref = getTransactionPreference(),
        filterPreference = getTransactionFilterPreference(),
        balancePreference = getTransactionBalancePreference(),
        localUser = getUserDao(),
        utilPreference = getUtilPreference(),
        connectInterceptor = getConnectInterceptor()
    )
}