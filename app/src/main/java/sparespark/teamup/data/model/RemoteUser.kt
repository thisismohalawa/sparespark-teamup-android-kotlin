package sparespark.teamup.data.model

import sparespark.teamup.core.ROLE_EMPLOYEE
import sparespark.teamup.core.map.DEACTIVATED


data class RemoteUser(
    val uid: String? = "",
    val name: String? = "",
    val email: String? = "",
    val phone: String? = "",
    val roleId: Int? = ROLE_EMPLOYEE,
    val activated: Boolean? = DEACTIVATED
)
