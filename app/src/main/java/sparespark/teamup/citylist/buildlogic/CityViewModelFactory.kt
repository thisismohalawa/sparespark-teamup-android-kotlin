package sparespark.teamup.citylist.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.citylist.CityViewModel
import sparespark.teamup.data.repository.CityRepository

class CityViewModelFactory(
    private val cityRepo: CityRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(CityViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            CityViewModel(cityRepo) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
