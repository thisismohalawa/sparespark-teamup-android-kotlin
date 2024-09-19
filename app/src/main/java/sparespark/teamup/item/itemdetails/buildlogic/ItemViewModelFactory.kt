package sparespark.teamup.item.itemdetails.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.ItemRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.item.itemdetails.ItemViewModel

class ItemViewModelFactory(
    private val itemRepo: ItemRepository,
    private val clientRepo: ClientRepository,
    private val preferenceRepo: PreferenceRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        if (modelClass.isAssignableFrom(ItemViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            ItemViewModel(
                itemRepo,
                clientRepo,
                preferenceRepo,
                extras.createSavedStateHandle()
            ) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}