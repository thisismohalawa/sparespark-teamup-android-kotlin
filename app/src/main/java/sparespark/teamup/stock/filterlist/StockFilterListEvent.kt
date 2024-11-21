package sparespark.teamup.stock.filterlist

sealed class StockFilterListEvent {
    data object OnViewStart : StockFilterListEvent()
    data object OnFilterBtnClick : StockFilterListEvent()
    data class OnSpinnerCitySelect(val pos: Int?) : StockFilterListEvent()
    data class OnSpinnerClientSelect(val pos: Int?) : StockFilterListEvent()
    data class OnSpinnerCompanySelect(val pos: Int?) : StockFilterListEvent()
    data class OnSpinnerProductSelect(val pos: Int?) : StockFilterListEvent()
    data class OnSearchQueryTextUpdate(val newText: String) : StockFilterListEvent()
}
