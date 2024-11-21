package sparespark.teamup.stock.itemsell

sealed class StockSellViewEvent {
    data object OnStartGetItem : StockSellViewEvent()
    data class OnTempSwitchCheck(val temp: Boolean) : StockSellViewEvent()
    data class OnClientAutoCompleteSelect(val client: String) : StockSellViewEvent()
    data class OnSpinnerStockProductSelect(val pos: Int) : StockSellViewEvent()
    data class OnUpdateBtnClick(val quantity: String, val note: String) : StockSellViewEvent()
}
