package sparespark.teamup.data.model.user

import sparespark.teamup.core.internal.DEACTIVATED
import sparespark.teamup.core.internal.ROLE_EMPLOYEE


data class RemoteUser(
    val uid: String? = "",
    val name: String? = "",
    val email: String? = "",
    val phone: String? = "",
    val roleId: Int? = ROLE_EMPLOYEE,
    val activated: Boolean? = DEACTIVATED
)
