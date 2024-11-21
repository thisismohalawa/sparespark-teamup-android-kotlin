package sparespark.teamup.auth

sealed class AuthEvent<out T> {
    data object GetAuthUser : AuthEvent<Nothing>()
    data object OnAuthBtnClick : AuthEvent<Nothing>()
    data class OnLoginBtnClick(val email: String, val pass: String) : AuthEvent<Nothing>()
    data class OnGoogleSignInResult<out LoginResult>(val result: LoginResult) :
        AuthEvent<LoginResult>()
    data class OnSignupBtnClick(val email: String, val pass: String) : AuthEvent<Nothing>()
}