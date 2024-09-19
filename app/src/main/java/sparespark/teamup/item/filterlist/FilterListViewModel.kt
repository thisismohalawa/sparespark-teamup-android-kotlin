package sparespark.teamup.item.filterlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.item.Item
import sparespark.teamup.data.model.statics.LStatics
import sparespark.teamup.data.model.statics.StaticsStates
import sparespark.teamup.data.preference.selector.LocalSelectorRepository
import sparespark.teamup.data.repository.ItemRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.StaticsRepository
import sparespark.teamup.item.BaseItemListViewModel

private const val SEARCH_MIN_QUERY = 3

class FilterListViewModel(
    itemRepo: ItemRepository,
    staticRepo: StaticsRepository,
    selectorRepo: LocalSelectorRepository,
    preferenceRepo: PreferenceRepository,
    savedStateHandle: SavedStateHandle
) : BaseItemListViewModel<FilterListEvent, Nothing>(
    itemRepo, staticRepo, selectorRepo, preferenceRepo
) {

    internal val exitState = MutableLiveData<Boolean>()
    internal val searchQueryTextState = MutableLiveData<String>()
    internal val activeListState = MutableLiveData<Boolean>()
    internal val adminListState = MutableLiveData<Boolean>()
    internal val buyListState = MutableLiveData<Boolean>()

    private val navSQuery: String? = savedStateHandle["search_query"]
    private val navActive: Boolean? = savedStateHandle["active_items"]
    private val navAdmin: Boolean? = savedStateHandle["admin_items"]
    private val navBuy: Boolean? = savedStateHandle["buy_items"]

    override fun handleViewEvent(event: FilterListEvent) {
        when (event) {
            is FilterListEvent.OnViewStart -> {
                clearSelectionSet()
                updateStatesNavState()
                updateItemListHintTitle()
                updateNewListStaticsUse()
                updateQueryNavState()
                checkIfActiveEnabled()
            }

            is FilterListEvent.OnSearchQueryTextUpdate -> getRemoteItemListByQuery(event.newText)

            is FilterListEvent.OnViewBackPressed -> viewBackPressed()

            is FilterListEvent.OnActiveRadioBtnCheck -> {
                if (isListExists()) activeFiltration()
                else {
                    clearSearchQuery()
                    getRemoteActiveList()
                }
                calculateListStatics()
            }

            is FilterListEvent.OnBuyRadioBtnCheck -> {
                if (isListExists()) buyFiltration()
                else {
                    clearSearchQuery()
                    getRemoteBuyList()
                }
                calculateListStatics()
            }

            is FilterListEvent.OnAdminRadioBtnCheck -> {
                if (isListExists()) adminFiltration()
                else {
                    clearSearchQuery()
                    getRemoteAdminList()
                }
                calculateListStatics()
            }
        }
    }

    private fun isListExists(): Boolean = itemListState.value?.isNotEmpty() == true

    private fun getRemoteItemListByQuery(query: String) = viewModelScope.launch {
        if (query.length < SEARCH_MIN_QUERY) return@launch
        showLoading()
        when (val result = itemRepo.getRemoteItemListByQuery(
            query = query
        )) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.cannot_read_remote_data)
            })

            is Result.Value -> checkFilterRadioStates(result.value.asReversed())
        }
        hideLoading()
    }

    private fun checkFilterRadioStates(list: List<Item>) = viewModelScope.launch {
        val result = when {
            activeListState.value == true -> itemRepo.filterToActiveList(list)

            buyListState.value == true -> itemRepo.filterToBuyList(list)

            adminListState.value == true -> itemRepo.filterToAdminList(list)

            else -> null
        }

        if (result == null) {
            itemListState.value = list
            calculateListStatics()
            return@launch
        }

        if (result is Result.Value) {
            itemListState.value = result.value
            calculateListStatics()
        } else showError(R.string.cannot_read_remote_data)
    }

    private fun getRemoteActiveList() = viewModelScope.launch {
        showLoading()
        when (val result = itemRepo.getActiveItemList()) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.cannot_read_remote_data)
            })

            is Result.Value -> itemListState.value = result.value.asReversed()

        }
        hideLoading()
    }

    private fun getRemoteBuyList() = viewModelScope.launch {
        showLoading()
        when (val result = itemRepo.getBuyItemList()) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.cannot_read_remote_data)
            })

            is Result.Value -> itemListState.value = result.value.asReversed()
        }
        hideLoading()
    }

    private fun getRemoteAdminList() = viewModelScope.launch {
        showLoading()
        when (val result = itemRepo.getAdminItemList()) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.cannot_read_remote_data)
            })

            is Result.Value -> itemListState.value = result.value.asReversed()
        }
        hideLoading()
    }

    private fun updateItemListHintTitle() = viewModelScope.launch {
        val result = itemRepo.getFilteredItemListHintTitle()
        if (result is Result.Value) updateItemListHintAttempt.value = result.value
    }

    private fun activeFiltration() = viewModelScope.launch {
        itemListState.value?.let {
            val result = itemRepo.filterToActiveList(it)
            if (result is Result.Value) itemListState.value = result.value
            else showError(R.string.cannot_read_local_data)
        }
    }

    private fun buyFiltration() = viewModelScope.launch {
        itemListState.value?.let {
            val result = itemRepo.filterToBuyList(it)
            if (result is Result.Value) itemListState.value = result.value
            else showError(R.string.cannot_read_local_data)
        }
    }

    private fun adminFiltration() = viewModelScope.launch {
        itemListState.value?.let {
            val result = itemRepo.filterToAdminList(it)
            if (result is Result.Value) itemListState.value = result.value
            else showError(R.string.cannot_read_local_data)
        }
    }

    private fun checkIfActiveEnabled() {
        if (activeListState.value == true &&
            searchQueryTextState.value.isNullOrEmpty()
        ) {
            getRemoteActiveList()
            calculateListStatics()
        }
    }

    private fun calculateListStatics() {
        if (useListStaticsState.value == true) itemListState.value?.calculateListStatics()
    }

    private fun updateStatesNavState() {
        activeListState.value = navActive == true
        adminListState.value = navAdmin == true
        buyListState.value = navBuy == true
    }

    private fun updateQueryNavState() {
        searchQueryTextState.value = navSQuery ?: ""
    }

    private fun updateNewListStaticsUse() {
        useListStaticsState.value = isListStaticsEnable()
        if (useListStaticsState.value == true) listStaticsState.value =
            LStatics(StaticsStates.CONNECTING)
    }

    private fun clearSearchQuery() {
        searchQueryTextState.value = ""
    }

    private fun viewBackPressed() {
        exitState.value = !isListExists()
        clearSearchQuery()
        updateNewListStaticsUse()
        activeListState.value = false
        buyListState.value = false
        adminListState.value = false
        itemListState.value = emptyList()
    }
}