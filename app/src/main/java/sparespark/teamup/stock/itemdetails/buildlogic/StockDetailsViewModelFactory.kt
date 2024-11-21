package sparespark.teamup.stock.itemdetails.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.ProductRepository
import sparespark.teamup.data.repository.StockRepository
import sparespark.teamup.stock.itemdetails.StockDetailsViewModel

class StockDetailsViewModelFactory(
    private val stockRepo: StockRepository,
    private val productRepo: ProductRepository,
    private val preferenceRepo: PreferenceRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        if (modelClass.isAssignableFrom(StockDetailsViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            StockDetailsViewModel(
                stockRepo,
                productRepo,
                preferenceRepo,
                extras.createSavedStateHandle()
            ) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}