package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.User

interface LoginRepository  {
    suspend fun getAuthUser(): Result<Exception, User?>
    suspend fun signInGoogleUser(idToken: String): Result<Exception, Unit>
    suspend fun signUpWithEmailAndPass(email: String, password: String): Result<Exception, Unit>
    suspend fun signInWithEmailAndPass(email: String, password: String): Result<Exception, Unit>
}
