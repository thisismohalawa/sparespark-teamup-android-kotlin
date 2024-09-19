package sparespark.teamup.item.filterlist.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import sparespark.teamup.data.preference.selector.LocalSelectorRepository
import sparespark.teamup.data.repository.ItemRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.StaticsRepository
import sparespark.teamup.item.filterlist.FilterListViewModel

class FilterListViewModelFactory(
    private val itemRepo: ItemRepository,
    private val staticsRepo: StaticsRepository,
    private val selectorRepo: LocalSelectorRepository,
    private val preferenceRepo: PreferenceRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        if (modelClass.isAssignableFrom(FilterListViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            FilterListViewModel(
                itemRepo, staticsRepo, selectorRepo, preferenceRepo, extras.createSavedStateHandle()
            ) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}

