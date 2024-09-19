package sparespark.teamup.item.filterlist

sealed class FilterListEvent {
    data object OnViewStart : FilterListEvent()
    data object OnViewBackPressed : FilterListEvent()
    data object OnBuyRadioBtnCheck : FilterListEvent()
    data object OnActiveRadioBtnCheck : FilterListEvent()
    data object OnAdminRadioBtnCheck : FilterListEvent()
    data class OnSearchQueryTextUpdate(val newText: String) : FilterListEvent()
}
