package sparespark.teamup.auh

interface AuthViewInteract {
    fun displayToast(msg: String)
    fun startDataActivity()
}

sealed class AuthEvent<out T> {
    data object GetAuthUser : AuthEvent<Nothing>()
    data object OnAuthBtnClick : AuthEvent<Nothing>()
    data class OnLoginBtnClick(val email: String, val pass: String) : AuthEvent<Nothing>()
    data class OnGoogleSignInResult<out LoginResult>(val result: LoginResult) :
        AuthEvent<LoginResult>()

    data object GetTestUserCredential : AuthEvent<Nothing>()
    data class OnSignupBtnClick(val email: String, val pass: String) : AuthEvent<Nothing>()
}
