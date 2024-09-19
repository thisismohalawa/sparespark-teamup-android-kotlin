package sparespark.teamup.data.network.connectivity

import okhttp3.Interceptor

interface ConnectivityInterceptor : Interceptor {
    fun isOnline(): Boolean
}
