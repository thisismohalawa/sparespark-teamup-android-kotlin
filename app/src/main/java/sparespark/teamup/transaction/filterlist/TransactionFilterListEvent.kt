package sparespark.teamup.transaction.filterlist

sealed class TransactionFilterListEvent {
    data object OnViewStart : TransactionFilterListEvent()
    data class OnSpinnerCitySelect(val pos: Int?) : TransactionFilterListEvent()
    data class OnSpinnerClientSelect(val pos: Int?) : TransactionFilterListEvent()
    data class OnSearchQueryTextUpdate(val newText: String) : TransactionFilterListEvent()
    data object OnTransactionFilterBtnClick : TransactionFilterListEvent()
}
