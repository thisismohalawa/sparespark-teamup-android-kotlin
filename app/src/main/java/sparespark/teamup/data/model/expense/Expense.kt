package sparespark.teamup.data.model.expense

data class Expense(
    var id: String,
    var creationDate: String,
    var createdBy: String,
    var name: String,
    var cost: Double,
    val note: String,
    var income: Boolean,
    var team:Boolean
)
