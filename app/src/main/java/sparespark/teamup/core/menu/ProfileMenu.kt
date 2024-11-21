package sparespark.teamup.core.menu

import sparespark.teamup.R
import sparespark.teamup.data.model.ProfileMenu

internal const val MENU_CITY = 1
internal const val MENU_CLIENT = 2
internal const val MENU_TEAM = 3
internal const val MENU_AUTH = 6
internal const val MENU_PRODUCT = 7
internal const val MENU_COMPANY = 8

internal fun menuList(userExist: Boolean) = listOf(
    ProfileMenu(
        id = MENU_CITY, title = R.string.city_list, des = R.string.update_summary
    ), ProfileMenu(
        id = MENU_CLIENT, title = R.string.client_list, des = R.string.update_summary
    ), ProfileMenu(
        id = MENU_COMPANY, title = R.string.company_list, des = R.string.update_summary
    ), ProfileMenu(
        id = MENU_PRODUCT, title = R.string.product_list, des = R.string.update_summary
    ), ProfileMenu(
        id = MENU_TEAM, title = R.string.team_list, des = R.string.team_summary
    ), ProfileMenu(
        id = MENU_AUTH,
        title = if (userExist) R.string.logout else R.string.login_status,
        des = null,
        isNav = false,
        isRedColored = true
    )
)
