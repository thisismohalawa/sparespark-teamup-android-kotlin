package sparespark.teamup.core.internal


internal const val ROLE_OWNER = 1
internal const val ROLE_ADMIN = 2
internal const val ROLE_EMPLOYEE = 3

internal const val CURRENT_USER_ID = 0

internal const val DEACTIVATED = false

internal const val SIGN_IN_REQUEST_CODE = 1227

internal fun Int?.isOwner(): Boolean = this?.equals(ROLE_OWNER) == true
internal fun Int?.isAdmin(): Boolean = this?.equals(ROLE_ADMIN) == true
