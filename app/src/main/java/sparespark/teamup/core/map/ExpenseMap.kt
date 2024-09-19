package sparespark.teamup.core.map

import sparespark.teamup.data.model.expense.Expense
import sparespark.teamup.data.model.expense.RemoteExpense
import sparespark.teamup.data.room.expense.RoomExpense


internal const val DEF_EXP_TEAM = false
internal const val DEF_EXP_INCOME = false


internal val RoomExpense.toExpense: Expense
    get() = Expense(
        id = this.id,
        creationDate = this.creationDate,
        createdBy = this.createdBy,
        name = this.name,
        note = this.note,
        cost = this.cost,
        income = this.income,
        team = this.team
    )
internal val RemoteExpense.toExpense: Expense
    get() = Expense(
        id = this.id ?: "",
        creationDate = this.creationDate ?: "",
        createdBy = this.createdBy ?: "",
        name = this.name ?: "",
        cost = this.cost ?: 0.0,
        note = this.note ?: "",
        income = this.income ?: DEF_EXP_INCOME,
        team = this.team ?: DEF_EXP_TEAM
    )
internal val Expense.toRemoteExpense: RemoteExpense
    get() = RemoteExpense(
        id = this.id,
        creationDate = this.creationDate,
        createdBy = this.createdBy,
        name = this.name,
        cost = this.cost,
        note = this.note,
        income = this.income,
        team = this.team
    )
internal val Expense.toRoomExpense: RoomExpense
    get() = RoomExpense(
        id = this.id,
        creationDate = this.creationDate,
        createdBy = this.createdBy,
        name = this.name,
        cost = this.cost,
        note = this.note,
        income = this.income,
        team = this.team
    )

internal fun List<RoomExpense>.toList(): List<Expense> = this.flatMap {
    listOf(it.toExpense)
}

