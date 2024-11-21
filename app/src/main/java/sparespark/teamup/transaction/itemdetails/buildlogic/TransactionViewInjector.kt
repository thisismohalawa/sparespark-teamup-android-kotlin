package sparespark.teamup.transaction.itemdetails.buildlogic

import android.app.Application
import sparespark.teamup.transaction.BaseTransactionInjector

class TransactionViewInjector(
    app: Application
) : BaseTransactionInjector(app) {
    fun provideViewModelFactory() = TransactionViewModelFactory(
        getTransactionRepository(),
        getClientRepository(),
        getPreferenceRepository()
    )
}
