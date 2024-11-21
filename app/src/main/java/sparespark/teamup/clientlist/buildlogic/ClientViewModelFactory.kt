package sparespark.teamup.clientlist.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.clientlist.ClientViewModel
import sparespark.teamup.data.repository.CityRepository
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.PreferenceRepository

class ClientViewModelFactory(
    private val clientRepo: ClientRepository,
    private val cityRepo: CityRepository,
    private val preferenceRepo: PreferenceRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(ClientViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            ClientViewModel(clientRepo, cityRepo, preferenceRepo) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
