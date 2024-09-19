package sparespark.teamup.item.itemlist

sealed class ItemListEvent {
    data object OnViewStart : ItemListEvent()
    data object OnViewBackPressed : ItemListEvent()
}
