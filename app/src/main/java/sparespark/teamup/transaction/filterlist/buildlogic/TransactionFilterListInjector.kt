package sparespark.teamup.transaction.filterlist.buildlogic

import android.app.Application
import sparespark.teamup.transaction.BaseTransactionInjector

class TransactionFilterListInjector(
    app: Application
) : BaseTransactionInjector(app) {
    fun provideViewModelFactory() = TransactionFilterListViewModelFactory(
        getTransactionRepository(),
        getCityRepository(),
        getClientRepository(),
        getPreferenceRepository()
    )
}
