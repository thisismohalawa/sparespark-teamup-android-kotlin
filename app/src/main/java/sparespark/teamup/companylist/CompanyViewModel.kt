package sparespark.teamup.companylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.DELAY_VIEW_EXPAND
import sparespark.teamup.core.base.BaseAdministrationViewModel
import sparespark.teamup.core.getSystemTimeMillis
import sparespark.teamup.core.isNewStringItem
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.company.Company
import sparespark.teamup.data.repository.CompanyRepository

class CompanyViewModel(
    private val companyRepo: CompanyRepository,
) : BaseAdministrationViewModel<CompanyEvent>(
    companyRepo = companyRepo,
    cityRepo = null,
    clientRepo = null,
    productRepo = null,
    preferenceRepo = null
) {
    internal val bottomSheetViewState = MutableLiveData<Int>()

    private val companyState = MutableLiveData<Company>()
    val company: LiveData<Company> get() = companyState

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState

    override fun handleEvent(event: CompanyEvent) {
        when (event) {
            is CompanyEvent.OnStartGetCompany -> setupCompany(pos = null)
            is CompanyEvent.OnListItemClick -> setupCompany(pos = event.pos)
            is CompanyEvent.OnListItemLongClick -> onListItemViewClick(pos = event.pos)
            is CompanyEvent.GetCompanyList -> getCompanyList()
            is CompanyEvent.HideBottomSheet -> hideBottomSheet()
            is CompanyEvent.OnMenuRefreshClick -> clearListCacheTime()
            is CompanyEvent.OnMenuDeleteClick -> deleteCompany(event.pos)
            is CompanyEvent.OnUpdateTxtClick -> updateCompany(event.company)
        }
    }

    private fun setupCompany(pos: Int?) = viewModelScope.launch {
        if (pos == null) companyState.value = Company("", "")
        else companyState.value = companyListState.value?.get(pos)
        delay(DELAY_VIEW_EXPAND)
        expandBottomSheet()
    }

    private fun clearListCacheTime() = viewModelScope.launch {
        showLoading()
        if (companyRepo.clearListCacheTime() is Result.Value) companyUpdated()
        else showError(R.string.cannot_update_local_entries)
        hideLoading()
    }

    private fun updateCompany(companyName: String) = viewModelScope.launch {
        companyState.value?.let {
            showLoading()
            if (it.id.isNewStringItem())
                companyState.value?.id = getSystemTimeMillis()

            when (val result = companyRepo.updateCompany(
                company = it.copy(name = companyName)
            )) {
                is Result.Error -> result.error.message.checkExceptionMsg(error = {
                    showError(R.string.cannot_update_entries)
                })

                is Result.Value -> companyUpdated()
            }
        }
        hideLoading()
    }

    private fun deleteCompany(pos: Int) = viewModelScope.launch {
        val id = companyListState.value?.get(pos)?.id
        if (id.isNullOrBlank()) return@launch

        showLoading()

        val result = when (isMultipleSelectionList(itemId = id)) {
            true -> companyRepo.deleteCompany(itemsIds = getSelectionList())

            false -> companyRepo.deleteCompany(itemId = id)
        }
        if (result is Result.Error) result.error.message.checkExceptionMsg(error = { showError(R.string.cannot_update_entries) }) else companyUpdated()
        hideLoading()
    }

    private fun onListItemViewClick(pos: Int) = viewModelScope.launch {
        val itemId = companyListState.value?.get(pos)?.id
        itemId?.let { updateSelection(itemId = it, itemPos = pos) }
    }

    private fun companyUpdated() {
        updatedState.value = Unit
    }

    private fun expandBottomSheet() {
        bottomSheetViewState.value = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideBottomSheet() {
        bottomSheetViewState.value = BottomSheetBehavior.STATE_HIDDEN
    }
}