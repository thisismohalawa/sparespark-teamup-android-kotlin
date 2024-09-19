package sparespark.teamup.userprofile.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.MENU_AUTH
import sparespark.teamup.core.MENU_CITY
import sparespark.teamup.core.MENU_CLIENT
import sparespark.teamup.core.MENU_PROFILE
import sparespark.teamup.core.MENU_TEAM
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.IMenu
import sparespark.teamup.data.repository.UserRepository
import sparespark.teamup.userprofile.UserEvent
import sparespark.teamup.userprofile.UserViewModel

class ProfileViewModel(
    val userRepo: UserRepository
) : UserViewModel(userRepo) {

    internal val loginAttempt = MutableLiveData<Unit>()

    private val menuListState = MutableLiveData<List<IMenu>>()
    val menuList: LiveData<List<IMenu>> get() = menuListState

    private val editMenuState = MutableLiveData<Event<Int>>()
    val editMenu: LiveData<Event<Int>> get() = editMenuState

    override fun handleEvent(event: UserEvent) {
        when (event) {
            is UserEvent.GetUser -> getUser(followAction = {
                getMenuList()
            })

            is UserEvent.OnMenuItemClick -> actionEditMenu(event.menuId)
            else -> Unit
        }
    }

    private fun getMenuList() {
        menuListState.value = menuList(
            userExist = isUserExist()
        )
    }

    private fun actionEditMenu(menuId: Int) {
        if (menuId == MENU_AUTH) signOut()
        else editMenuState.value = Event(menuId)
    }

    private fun signOut() = viewModelScope.launch {
        showLoading()
        when (val result = userRepo.signOutCurrentUser()) {
            is Result.Error -> result.error.message.actionExceptionMsg(
                offline = {
                    showError(R.string.no_internet)
                },
                unauthorised = {
                    loginAttempt.value = Unit
                },
                error = {
                    showError(R.string.unable_to_sign_out)
                })

            is Result.Value -> userUpdated()
        }
        hideLoading()
    }
}

private fun menuList(userExist: Boolean) = listOf(
    IMenu(
        id = MENU_PROFILE,
        title = R.string.update_profile,
    ), IMenu(
        id = MENU_CITY, title = R.string.city_list, des = R.string.update_summary
    ), IMenu(
        id = MENU_CLIENT, title = R.string.client_list, des = R.string.update_summary
    ), IMenu(
        id = MENU_TEAM, title = R.string.my_team, des = R.string.team_summary
    ), IMenu(
        id = MENU_AUTH,
        title = if (userExist) R.string.logout else R.string.login_status,
        des = null,
        isNav = false,
        isRedColored = true
    )
)