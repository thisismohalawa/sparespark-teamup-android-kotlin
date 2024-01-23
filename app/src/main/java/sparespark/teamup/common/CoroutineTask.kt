package sparespark.teamup.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend fun <T> launchASuspendTaskScope(
    taskCall: suspend () -> T,
): T = withContext(Dispatchers.IO) {
    taskCall.invoke()
}
