package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.user.User

interface UserRepository {
    suspend fun getLocalUser(): Result<Exception, User?>
    suspend fun getRemoteUser(): Result<Exception, User?>
    suspend fun checkIfLoginRequired(): Result<Exception, Boolean>
    suspend fun checkIfPushRequired(): Result<Exception, Boolean>
    suspend fun updateLocalUser(user: User): Result<Exception, Unit>
    suspend fun pushUserToRemoteServer(user: User): Result<Exception, Unit>
    suspend fun updateLastLogin(): Result<Exception, Unit>
    suspend fun updateRemoteUser(name: String, phone: String): Result<Exception, Unit>
    suspend fun signOutCurrentUser(): Result<Exception, Unit>
}