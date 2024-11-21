package sparespark.teamup.transaction.itemdetails.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.TransactionRepository
import sparespark.teamup.transaction.itemdetails.TransactionDetailsViewModel

class TransactionViewModelFactory(
    private val itemRepo: TransactionRepository,
    private val clientRepo: ClientRepository,
    private val preferenceRepo: PreferenceRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        if (modelClass.isAssignableFrom(TransactionDetailsViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            TransactionDetailsViewModel(
                itemRepo,
                clientRepo,
                preferenceRepo,
                extras.createSavedStateHandle()
            ) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}