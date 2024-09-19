package sparespark.teamup.team.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sparespark.teamup.data.repository.TeamRepository
import sparespark.teamup.team.TeamViewModel

class TeamViewModelFactory(
    private val teamRepo: TeamRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        if (modelClass.isAssignableFrom(TeamViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            TeamViewModel(teamRepo) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
