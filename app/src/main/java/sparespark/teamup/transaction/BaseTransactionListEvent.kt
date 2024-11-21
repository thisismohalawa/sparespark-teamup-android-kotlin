package sparespark.teamup.transaction

sealed class BaseTransactionListEvent {
    data object OnMenuItemListRefresh : BaseTransactionListEvent()
    data class OnItemListLongClick(val pos: Int) : BaseTransactionListEvent()
    data class OnMenuItemListPushClick(val pos: Int) : BaseTransactionListEvent()
    data class OnMenuItemListDeleteClick(var pos: Int) : BaseTransactionListEvent()
    data class OnMenuItemListCopyClick(val pos: Int) : BaseTransactionListEvent()
    data class OnMenuItemListShareClick(val pos: Int) : BaseTransactionListEvent()
    data class OnMenuItemUpdateClick(val pos: Int) : BaseTransactionListEvent()
    data object OnMenuItemListExportClick : BaseTransactionListEvent()
    data class OnItemListCheckboxClick(val pos: Int, val isActive: Boolean) :
        BaseTransactionListEvent()
}