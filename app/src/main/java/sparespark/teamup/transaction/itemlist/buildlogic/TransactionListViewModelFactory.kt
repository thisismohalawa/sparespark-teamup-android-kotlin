package sparespark.teamup.transaction.itemlist.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.data.repository.NoteRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.TransactionRepository
import sparespark.teamup.transaction.itemlist.TransactionListViewModel

class TransactionListViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val preferenceRepository: PreferenceRepository?,
    private val noteRepository: NoteRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(TransactionListViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            TransactionListViewModel(
                transactionRepository,
                preferenceRepository,
                noteRepository
            ) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
