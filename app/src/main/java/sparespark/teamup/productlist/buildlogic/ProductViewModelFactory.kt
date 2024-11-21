package sparespark.teamup.productlist.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.data.repository.CompanyRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.ProductRepository
import sparespark.teamup.productlist.ProductViewModel

class ProductViewModelFactory(
    private val productRepo: ProductRepository,
    private val companyRepo: CompanyRepository,
    private val preferenceRepo: PreferenceRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(ProductViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            ProductViewModel(productRepo, companyRepo, preferenceRepo) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
