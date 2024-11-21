package sparespark.teamup.core.internal

import android.content.Context
import sparespark.teamup.R


internal fun Int.toRoleTitle(context: Context): String = when (this) {
    ROLE_OWNER -> context.getString(R.string.owner)
    ROLE_ADMIN -> context.getString(R.string.admin)
    else -> context.getString(R.string.team)
}