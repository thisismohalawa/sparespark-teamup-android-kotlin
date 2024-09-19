package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.team.Team

interface TeamRepository {
    suspend fun getTeamList(): Result<Exception, List<Team>>
    suspend fun updateTeamActiveStatus(uid: String, isActive: Boolean): Result<Exception, Unit>
}
