package sparespark.teamup.core.wrapper

import android.content.Context
import androidx.annotation.StringRes

sealed class UIResource {

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UIResource()


    fun asString(context: Context?): String? {
        return when (this) {
            is StringResource -> context?.getString(resId, *args)
        }
    }
}
