package sparespark.teamup.companylist.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.companylist.CompanyViewModel
import sparespark.teamup.data.repository.CompanyRepository

class CompanyViewModelFactory(
    private val companyRepo: CompanyRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(CompanyViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            CompanyViewModel(companyRepo) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
