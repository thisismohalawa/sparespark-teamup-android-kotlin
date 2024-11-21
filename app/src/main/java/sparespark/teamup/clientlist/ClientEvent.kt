package sparespark.teamup.clientlist

import sparespark.teamup.citylist.CityEvent


sealed class ClientEvent {
    data object OnStartGetClient : ClientEvent()
    data object GetClientList : ClientEvent()
    data object HideBottomSheet : ClientEvent()
    data class OnSpinnerCitySelect(val pos: Int) : ClientEvent()
    data class OnListItemClick(val pos: Int) : ClientEvent()
    data class OnListItemLongClick(val pos: Int) : ClientEvent()
    data class OnUpdateTxtClick(val name: String, val phone: String) : ClientEvent()
    data object OnMenuRefreshClick : ClientEvent()
    data class OnMenuDeleteClick(val pos: Int) : ClientEvent()
    data class OnMenuDialClick(val pos: Int) : ClientEvent()
}
