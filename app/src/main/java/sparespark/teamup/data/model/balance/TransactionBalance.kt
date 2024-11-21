package sparespark.teamup.data.model.balance

import androidx.annotation.StringRes


data class TransactionBalance(
    var id: Int,
    var total: Double,
    var isCost: Boolean? = null,
    var isSell: Boolean? = null,
    @StringRes var desRes: Int? = null
)
