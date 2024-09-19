package sparespark.teamup.item

sealed class BaseItemListEvent {
    data class OnItemListClick(val pos: Int) : BaseItemListEvent()
    data class OnItemListCheckboxClick(val pos: Int, val isActive: Boolean) : BaseItemListEvent()
    data class OnItemListLongClick(val pos: Int) : BaseItemListEvent()
    data class OnItemListSingleClick(val pos: Int) : BaseItemListEvent()
    data class OnMenuItemListDeleteClick(var pos: Int) : BaseItemListEvent()
    data class OnMenuItemListCopyClick(val pos: Int) : BaseItemListEvent()
    data class OnMenuItemListShareClick(val pos: Int) : BaseItemListEvent()
    data object OnMenuItemListRefresh : BaseItemListEvent()
    data object OnMenuItemListExportClick : BaseItemListEvent()
}