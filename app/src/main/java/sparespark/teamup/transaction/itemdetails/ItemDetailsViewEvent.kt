package sparespark.teamup.transaction.itemdetails

sealed class ItemDetailsViewEvent {
    data object OnStartGetItem : ItemDetailsViewEvent()
    data class OnUpdateBtnClick(
        val price: String,
        val quantity: String,
        val note: String
    ) : ItemDetailsViewEvent()

    data class OnTotalPriceTxtClick(
        val price: String,
        val quantity: String
    ) : ItemDetailsViewEvent()

    data class OnQuantityTxtClick(
        val price: String,
        val total: String
    ) : ItemDetailsViewEvent()

    data class OnTempSwitchCheck(
        val temp: Boolean
    ) : ItemDetailsViewEvent()

    data class OnSellSwitchCheck(
        val sell: Boolean
    ) : ItemDetailsViewEvent()

    data class OnClientAutoCompleteSelect(val client: String) : ItemDetailsViewEvent()
}
