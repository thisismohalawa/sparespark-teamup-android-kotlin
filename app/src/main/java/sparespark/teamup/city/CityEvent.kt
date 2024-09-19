package sparespark.teamup.city

sealed class CityEvent {
    data object OnStartGetCity : CityEvent()
    data object GetCityList : CityEvent()
    data object HideBottomSheet : CityEvent()
    data class OnUpdateTxtClick(val city: String) : CityEvent()
    data class OnListItemClick(val pos: Int) : CityEvent()
    data object OnMenuRefreshClick : CityEvent()
    data class OnMenuDeleteClick(val pos: Int) : CityEvent()
    data class OnMenuNavigateCityClick(val pos: Int) : CityEvent()
}
