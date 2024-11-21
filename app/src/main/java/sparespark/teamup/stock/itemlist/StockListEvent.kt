package sparespark.teamup.stock.itemlist

sealed class StockListEvent {
    data object OnStartGetStockList : StockListEvent()
    data object OnSellBtnClick : StockListEvent()

}