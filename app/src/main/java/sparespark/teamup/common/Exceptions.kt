package sparespark.teamup.common

import java.io.IOException

const val NO_INTERNET_CONNECTION = "No Internet Connection."

class NoConnectivityException : IOException(NO_INTERNET_CONNECTION)
