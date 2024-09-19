package sparespark.teamup.data.model.statics

import androidx.annotation.StringRes

enum class StaticsStates(@StringRes var value: Int = 0) {
    CONNECTING,
    UN_AUTH,
    DISABLE,
    NOT_PERMITTED,
    DEACTIVATED,
    SUCCESS;
}