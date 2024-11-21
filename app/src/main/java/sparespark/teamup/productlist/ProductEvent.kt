package sparespark.teamup.productlist

sealed class ProductEvent {
    data object OnStartGetProduct : ProductEvent()
    data object GetProductList : ProductEvent()
    data object HideBottomSheet : ProductEvent()
    data class OnSpinnerCompanySelect(val pos: Int) : ProductEvent()
    data class OnUpdateTxtClick(val product: String) : ProductEvent()
    data class OnListItemClick(val pos: Int) : ProductEvent()
    data class OnListItemLongClick(val pos: Int) : ProductEvent()
    data object OnMenuRefreshClick : ProductEvent()
    data class OnMenuDeleteClick(val pos: Int) : ProductEvent()
}
