package sparespark.teamup.auh.signup.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.auh.signup.SignUpViewModel
import sparespark.teamup.data.repository.LoginRepository

class SignUpViewModelFactory(
    private val loginRepo: LoginRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            SignUpViewModel(loginRepo) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}