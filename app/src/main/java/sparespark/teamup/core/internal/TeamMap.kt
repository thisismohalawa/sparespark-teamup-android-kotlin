package sparespark.teamup.core.internal

import sparespark.teamup.data.model.team.RemoteTeam
import sparespark.teamup.data.model.team.Team

internal val RemoteTeam.toTeam: Team
    get() = Team(
        uid = this.uid ?: "",
        name = this.name ?: "",
        email = this.email ?: "",
        roleId = this.roleId ?: ROLE_EMPLOYEE,
        activated = this.activated ?: DEACTIVATED,
        lastLogin = this.lastLogin ?: ""
    )

