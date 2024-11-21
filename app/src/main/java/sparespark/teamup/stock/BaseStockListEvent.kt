package sparespark.teamup.stock

sealed class BaseStockListEvent {
    data object OnMenuItemRefresh : BaseStockListEvent()
    data class OnListItemLongClick(val pos: Int) : BaseStockListEvent()
    data class OnMenuItemPushClick(val pos: Int) : BaseStockListEvent()
    data class OnMenuItemDeleteClick(var pos: Int) : BaseStockListEvent()
    data class OnMenuItemCopyClick(val pos: Int) : BaseStockListEvent()
    data class OnMenuItemShareClick(val pos: Int) : BaseStockListEvent()
    data class OnMenuItemUpdateClick(val pos: Int) : BaseStockListEvent()
    data object OnMenuItemExportClick : BaseStockListEvent()

}