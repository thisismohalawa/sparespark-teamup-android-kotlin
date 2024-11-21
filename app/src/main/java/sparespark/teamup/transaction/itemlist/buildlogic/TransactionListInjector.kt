package sparespark.teamup.transaction.itemlist.buildlogic

import android.app.Application
import sparespark.teamup.transaction.BaseTransactionInjector

class TransactionListInjector(
    app: Application
) : BaseTransactionInjector(app) {
    fun provideViewModelFactory() = TransactionListViewModelFactory(
        getTransactionRepository(),
        getPreferenceRepository(),
        getNoteRepository()
    )
}
