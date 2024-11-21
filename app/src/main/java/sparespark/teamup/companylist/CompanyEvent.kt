package sparespark.teamup.companylist

sealed class CompanyEvent {
    data object OnStartGetCompany : CompanyEvent()
    data object GetCompanyList : CompanyEvent()
    data object HideBottomSheet : CompanyEvent()
    data class OnUpdateTxtClick(val company: String) : CompanyEvent()
    data class OnListItemClick(val pos: Int) : CompanyEvent()
    data class OnListItemLongClick(val pos: Int) : CompanyEvent()
    data object OnMenuRefreshClick : CompanyEvent()
    data class OnMenuDeleteClick(val pos: Int) : CompanyEvent()
}
