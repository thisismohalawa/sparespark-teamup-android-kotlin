package sparespark.teamup.userprofile.profiledetails.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.data.repository.UserRepository
import sparespark.teamup.userprofile.profiledetails.ProfileDetailViewModel

class ProfileDetailViewModelFactory(
    private val userRepo: UserRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(ProfileDetailViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            ProfileDetailViewModel(userRepo) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
