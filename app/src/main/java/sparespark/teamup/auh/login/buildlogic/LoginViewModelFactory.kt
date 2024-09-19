package sparespark.teamup.auh.login.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.auh.login.LoginViewModel
import sparespark.teamup.data.repository.LoginRepository
import sparespark.teamup.data.repository.UserRepository

class LoginViewModelFactory(
    private val loginRepo: LoginRepository,
    private val userRepo: UserRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(LoginViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            LoginViewModel(loginRepo, userRepo) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
