package sparespark.teamup.expense

sealed class ExpenseEvent {
    data object OnStartGetItem : ExpenseEvent()
    data object GetExpenseList : ExpenseEvent()
    data object HideBottomSheet : ExpenseEvent()
    data class OnSwitchIncomeUpdate(val income: Boolean) : ExpenseEvent()
    data class OnSwitchTeamUpdate(val team: Boolean) : ExpenseEvent()
    data class OnAutoCompleteNameSelect(val name: String) : ExpenseEvent()
    data class OnListItemClick(val pos: Int) : ExpenseEvent()
    data class OnUpdateTxtClick(val name: String, val cost: String, val note: String) :
        ExpenseEvent()
    data object OnMenuListRefresh : ExpenseEvent()
    data object OnMenuExportClick : ExpenseEvent()
    data class OnMenuDeleteClick(val pos: Int) : ExpenseEvent()
}
