package sparespark.teamup.stock.itemlist.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.StockRepository
import sparespark.teamup.stock.itemlist.StockListViewModel

class StockListViewModelFactory(
    private val stockRepo: StockRepository,
    private val preferenceRepo: PreferenceRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(StockListViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            StockListViewModel(stockRepo, preferenceRepo) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
