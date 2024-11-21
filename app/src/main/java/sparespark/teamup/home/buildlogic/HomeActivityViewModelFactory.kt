package sparespark.teamup.home.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import sparespark.teamup.data.repository.UserRepository
import sparespark.teamup.home.HomeActivityViewModel

class HomeActivityViewModelFactory(
    private val userRepo: UserRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        if (modelClass.isAssignableFrom(HomeActivityViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            HomeActivityViewModel(userRepo) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
