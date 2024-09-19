package sparespark.teamup.data.model.team

import sparespark.teamup.core.ROLE_EMPLOYEE
import sparespark.teamup.core.map.DEACTIVATED


data class RemoteTeam(
    val uid: String? = "",
    val name: String? = "",
    val email: String? = "",
    val roleId: Int? = ROLE_EMPLOYEE,
    val activated: Boolean? = DEACTIVATED,
    val lastLogin: String? = ""
)
