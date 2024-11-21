package sparespark.teamup.userprofile

sealed class UserEvent {
    data object GetCurrentUser : UserEvent()
    data class OnMenuItemClick(val menuId: Int) : UserEvent()
    data class OnUpdateBtnClick(
        val name: String,
        val phone: String
    ) : UserEvent()
}
