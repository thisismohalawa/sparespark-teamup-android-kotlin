package sparespark.teamup.item.itemdetails

sealed class ItemViewEvent {
    data object OnStartGetItem : ItemViewEvent()
    data class OnUpdateBtnClick(
        val price: String,
        val quantity: String,
        val note: String
    ) : ItemViewEvent()

    data class OnTotalPriceTxtClick(
        val price: String,
        val quantity: String
    ) : ItemViewEvent()

    data class OnQuantityTxtClick(
        val price: String,
        val total: String
    ) : ItemViewEvent()

    data class OnAdminSwitchCheck(
        val admin: Boolean
    ) : ItemViewEvent()

    data class OnSellSwitchCheck(
        val sell: Boolean
    ) : ItemViewEvent()

    data class OnClientAutoCompleteSelect(val client: String) : ItemViewEvent()
}
