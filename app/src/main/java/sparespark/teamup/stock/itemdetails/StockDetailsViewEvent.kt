package sparespark.teamup.stock.itemdetails


sealed class StockDetailsViewEvent {
    data object OnStartGetItem : StockDetailsViewEvent()
    data class OnTempSwitchCheck(val temp: Boolean) : StockDetailsViewEvent()
    data class OnStockAutoCompleteSelect(val productCompany: String) : StockDetailsViewEvent()
    data class OnUpdateBtnClick(val quantity: String, val note: String) : StockDetailsViewEvent()
}
