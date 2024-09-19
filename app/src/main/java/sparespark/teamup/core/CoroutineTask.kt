package sparespark.teamup.core

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal suspend fun <T> launchASuspendTaskScope(
    taskCall: suspend () -> T,
): T = withContext(Dispatchers.IO) {
    taskCall.invoke()
}

internal suspend fun <T> awaitTaskCompletable(task: Task<T>): Unit =
    suspendCoroutine { continuation ->
        task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(Unit)
            } else {
                continuation.resumeWithException(task.exception!!)
            }
        }
    }

internal suspend fun <T> awaitTaskResult(task: Task<T>): T = suspendCoroutine { continuation ->
    task.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result!!)
        } else {
            continuation.resumeWithException(task.exception!!)
        }
    }
}
