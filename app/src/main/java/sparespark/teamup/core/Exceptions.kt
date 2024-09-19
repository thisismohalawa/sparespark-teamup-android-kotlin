package sparespark.teamup.core

import java.io.IOException

const val UNAUTHORIZED = "unauthorized."
const val USER_DEACTIVATED = "Deactivated."
const val NOT_PERMITTED = "Not Permitted."
const val SERVER_DISABLE = "Server Disabled."
const val NO_INTERNET_CONNECTION = "No Internet Connection."

class UnAuthorizedException : Exception(UNAUTHORIZED)
class DeactivatedException : Exception(USER_DEACTIVATED)
class NotPermittedException : Exception(NOT_PERMITTED)
class ServerDisableException : Exception(SERVER_DISABLE)
class NoConnectivityException : IOException(NO_INTERNET_CONNECTION)