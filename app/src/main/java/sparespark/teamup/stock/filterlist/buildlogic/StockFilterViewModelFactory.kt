package sparespark.teamup.stock.filterlist.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.data.repository.CityRepository
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.CompanyRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.ProductRepository
import sparespark.teamup.data.repository.StockRepository
import sparespark.teamup.stock.filterlist.StockFilterViewModel

class StockFilterViewModelFactory(
    private val stockRepo: StockRepository,
    private val cityRepo: CityRepository,
    private val clientRepo: ClientRepository,
    private val companyRepo: CompanyRepository,
    private val productRepo: ProductRepository,
    private val preferenceRepo: PreferenceRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(StockFilterViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            StockFilterViewModel(
                stockRepo,
                cityRepo,
                clientRepo,
                companyRepo,
                productRepo,
                preferenceRepo
            ) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
