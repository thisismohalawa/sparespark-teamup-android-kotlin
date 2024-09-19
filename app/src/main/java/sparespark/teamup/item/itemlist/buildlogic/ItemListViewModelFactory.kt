package sparespark.teamup.item.itemlist.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.data.preference.selector.LocalSelectorRepository
import sparespark.teamup.data.repository.ItemRepository
import sparespark.teamup.data.repository.NoteRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.StaticsRepository
import sparespark.teamup.item.itemlist.ItemListViewModel

class ItemListViewModelFactory(
    private val itemRepo: ItemRepository,
    private val staticsRepo: StaticsRepository,
    private val selectorRepo: LocalSelectorRepository,
    private val noteRep: NoteRepository,
    private val preferenceRepo: PreferenceRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(ItemListViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            ItemListViewModel(itemRepo, staticsRepo, selectorRepo, preferenceRepo, noteRep) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
