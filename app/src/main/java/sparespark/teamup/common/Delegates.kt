package sparespark.teamup.common

import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
fun <T> lazyDeferred(block: suspend CoroutineScope.() -> T): Lazy<Deferred<T>> {
    return lazy {
        GlobalScope.async(start = CoroutineStart.LAZY) {
            block.invoke(this)
        }
    }
}
