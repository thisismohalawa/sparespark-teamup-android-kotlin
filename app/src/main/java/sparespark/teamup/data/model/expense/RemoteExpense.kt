package sparespark.teamup.data.model.expense

import sparespark.teamup.core.map.DEF_EXP_INCOME
import sparespark.teamup.core.map.DEF_EXP_TEAM

data class RemoteExpense(
    val id: String? = "",
    val creationDate: String? = "",
    var createdBy: String? = "",
    var name: String? = "",
    var cost: Double? = 0.0,
    val note: String? = "",
    var income: Boolean? = DEF_EXP_INCOME,
    var team: Boolean? = DEF_EXP_TEAM
)
