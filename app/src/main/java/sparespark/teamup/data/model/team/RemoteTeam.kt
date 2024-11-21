package sparespark.teamup.data.model.team

import sparespark.teamup.core.internal.DEACTIVATED
import sparespark.teamup.core.internal.ROLE_EMPLOYEE


data class RemoteTeam(
    val uid: String? = "",
    val name: String? = "",
    val email: String? = "",
    val roleId: Int? = ROLE_EMPLOYEE,
    val activated: Boolean? = DEACTIVATED,
    val lastLogin: String? = ""
)
