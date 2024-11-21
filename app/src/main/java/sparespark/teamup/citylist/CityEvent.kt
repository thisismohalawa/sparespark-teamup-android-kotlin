package sparespark.teamup.citylist

sealed class CityEvent {
    data object OnStartGetCity : CityEvent()
    data object GetCityList : CityEvent()
    data object HideBottomSheet : CityEvent()
    data class OnUpdateTxtClick(val name: String) : CityEvent()
    data class OnListItemClick(val pos: Int) : CityEvent()
    data class OnListItemLongClick(val pos: Int) : CityEvent()
    data object OnMenuRefreshClick : CityEvent()
    data class OnMenuDeleteClick(val pos: Int) : CityEvent()
}
