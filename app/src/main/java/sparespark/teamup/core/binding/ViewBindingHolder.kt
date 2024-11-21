package sparespark.teamup.core.binding

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

interface ViewBindingHolder<T : ViewBinding> {

    val binding: T?

    fun initBinding(binding: T, fragment: Fragment, onBound: (T.() -> Unit)?): View

    fun requireBinding(block: (T.() -> Unit)? = null): T

    fun destroyBinding()
}
