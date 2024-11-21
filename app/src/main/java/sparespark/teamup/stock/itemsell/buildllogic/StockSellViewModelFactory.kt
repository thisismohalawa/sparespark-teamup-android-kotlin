package sparespark.teamup.stock.itemsell.buildllogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.StockRepository
import sparespark.teamup.stock.itemsell.StockSellViewModel

class StockSellViewModelFactory(
    private val stockRepo: StockRepository,
    private val clientRepo: ClientRepository,
    private val preferenceRepo: PreferenceRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(StockSellViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            StockSellViewModel(
                stockRepo,
                clientRepo,
                preferenceRepo
            ) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}