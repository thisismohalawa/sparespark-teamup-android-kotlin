package sparespark.teamup.item

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.launchASuspendTaskScope
import sparespark.teamup.core.lazyDeferred
import sparespark.teamup.core.toShareText
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.ISelect
import sparespark.teamup.data.model.item.Item
import sparespark.teamup.data.model.statics.LStatics
import sparespark.teamup.data.model.statics.StaticsStates
import sparespark.teamup.data.preference.selector.LocalSelectorRepository
import sparespark.teamup.data.repository.ItemRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.StaticsRepository

open class BaseItemListViewModel<VE, AVE>(
    protected val itemRepo: ItemRepository,
    protected val staticsRepo: StaticsRepository,
    private val selectorRepo: LocalSelectorRepository,
    preferenceRepository: PreferenceRepository
) : BaseItemListEventViewModel<BaseItemListEvent, VE, AVE>(preferenceRepository) {

    override fun handleViewEvent(event: VE) = Unit

    override fun handleAttachViewEvent(event: AVE) = Unit

    override fun handleEvent(event: BaseItemListEvent) {
        when (event) {
            is BaseItemListEvent.OnItemListClick -> editItem(event.pos)
            is BaseItemListEvent.OnItemListCheckboxClick -> {
                activateItem(event.pos, event.isActive)
                clearStaticsCacheTime()
            }

            is BaseItemListEvent.OnItemListLongClick -> onItemListViewClick(event.pos)
            is BaseItemListEvent.OnItemListSingleClick -> onItemListViewClick(event.pos)

            is BaseItemListEvent.OnMenuItemListRefresh -> {
                clearStaticsCacheTime()
                clearListCacheTime()
            }

            is BaseItemListEvent.OnMenuItemListDeleteClick -> {
                deleteItem(event.pos)
                clearStaticsCacheTime()
            }

            is BaseItemListEvent.OnMenuItemListCopyClick -> copyItemList(event.pos)
            is BaseItemListEvent.OnMenuItemListShareClick -> shareItemList(event.pos)
            is BaseItemListEvent.OnMenuItemListExportClick -> exportItemList()
        }
    }

    private suspend fun getSelectionSet() = when (val result = selectorRepo.getSelectedSet()) {
        is Result.Error -> {
            showError(R.string.cannot_read_local_data)
            arrayListOf()
        }

        is Result.Value -> result.value.toMutableList()
    }

    private suspend fun String.isMultipleSelected(): Boolean {
        val deferredSelectionSet by lazyDeferred {
            getSelectionSet()
        }
        return when (deferredSelectionSet.await().size > 1) {
            true -> deferredSelectionSet.await().contains(this@isMultipleSelected)
            false -> false
        }
    }

    protected fun clearSelectionSet(unselectItems: Unit? = null) = viewModelScope.launch {
        val deferredSelectionSet by lazyDeferred {
            getSelectionSet()
        }
        deferredSelectionSet.await().let {
            if (it.size == 0) return@launch


            if (unselectItems != null) updateItemListSelectAttempt.value =
                it.toItemsPosList(itemListState.value)
        }

        if (selectorRepo.clearSelectedSet() is Result.Error)
            showError(R.string.cannot_read_local_data)
    }

    private fun getLocalList(localOnly: Unit?) = viewModelScope.launch {
        val result = itemRepo.getItemList(localOnly = localOnly)
        if (result is Result.Value) itemListState.value = result.value
        else showError(R.string.error_retrieve_data)
    }

    protected fun getItemList(resultAction: (() -> Unit)? = null) = viewModelScope.launch {
        showLoading()
        when (val result = itemRepo.getItemList()) {
            is Result.Error -> result.error.message.actionExceptionMsg(offline = {
                getLocalList(localOnly = Unit)
            }, unauthorised = {
                showError(R.string.unauthorized)
            }, deactivated = {
                showError(R.string.deactivated)
            }, error = {
                showError(R.string.error_retrieve_itemlist)
            })

            is Result.Value -> {
                itemListState.value = result.value.asReversed()
                resultAction?.invoke()
            }
        }
        hideLoading()
    }

    private fun activateItem(pos: Int, active: Boolean) = viewModelScope.launch {
        val id = itemListState.value?.get(pos)?.id
        if (id.isNullOrBlank()) return@launch

        showLoading()
        val deferredSelectionSet by lazyDeferred {
            getSelectionSet()
        }
        val result = when (id.isMultipleSelected()) {
            true -> itemRepo.activateItem(
                itemsIds = deferredSelectionSet.await(), isActive = active
            )

            false -> itemRepo.activateItem(itemId = id, isActive = active)
        }
        if (result is Result.Error) result.error.message.checkExceptionMsg(error = { showError(R.string.cannot_update_entries) })
        else itemListUpdated()
        hideLoading()
    }

    private fun deleteItem(pos: Int) = viewModelScope.launch {
        val id = itemListState.value?.get(pos)?.id
        if (id.isNullOrBlank()) return@launch

        showLoading()
        val deferredSelectionSet by lazyDeferred {
            getSelectionSet()
        }
        val result = when (id.isMultipleSelected()) {
            true -> itemRepo.deleteItem(itemsIds = deferredSelectionSet.await())

            false -> itemRepo.deleteItem(itemId = id)
        }
        if (result is Result.Error) result.error.message.checkExceptionMsg(error = { showError(R.string.cannot_update_entries) }) else itemListUpdated()
        hideLoading()
    }

    private fun onItemListViewClick(pos: Int) = viewModelScope.launch {
        val itemId = itemListState.value?.get(pos)?.id
        if (itemId.isNullOrBlank()) return@launch

        suspend fun removeSelectionItem(itemId: String) {
            selectorRepo.removeSelector(itemId)
        }

        suspend fun addSelectionItem(itemId: String) {
            selectorRepo.addSelector(itemId)
        }

        when (val result = selectorRepo.isSelectedSet(itemId)) {
            is Result.Error -> showError(R.string.cannot_read_local_data)
            is Result.Value -> {
                updateItemSelectAttempt.value = ISelect(pos, result.value)
                if (result.value) removeSelectionItem(itemId)
                else addSelectionItem(itemId)
            }
        }
    }

    protected fun getListStatics() = viewModelScope.launch {
        when (val result = staticsRepo.getListStatics()) {
            is Result.Error -> result.error.message.actionExceptionMsg(
                unauthorised = {
                    itemListState.value?.calculateListStatics()
                },
                offline = {
                    listStaticsState.value = LStatics(StaticsStates.CONNECTING)
                },
                deactivated = {
                    listStaticsState.value = LStatics(StaticsStates.DEACTIVATED)
                },
                notPermitted = {
                    listStaticsState.value = LStatics(StaticsStates.NOT_PERMITTED)
                },
                serveDisable = {
                    itemListState.value?.calculateListStatics()
                },
                error = {
                    listStaticsState.value = LStatics(StaticsStates.CONNECTING)
                    showError(R.string.cannot_read_statistics)
                }
            )

            is Result.Value -> listStaticsState.value = result.value
        }

    }

    protected fun List<Item>.calculateListStatics() = viewModelScope.launch {
        val result = staticsRepo.calculateListStatics(this@calculateListStatics)
        if (result is Result.Value) listStaticsState.value = result.value
        else showError(R.string.cannot_read_statistics)
    }

    private fun copyItemList(pos: Int) = viewModelScope.launch {
        val item = itemListState.value?.get(pos) ?: return@launch
        val deferredSelectionSet by lazyDeferred {
            getSelectionSet()
        }
        if (item.id.isMultipleSelected()) {
            var text = ""
            deferredSelectionSet.await().forEach { sId ->
                val listItem = itemListState.value?.single {
                    it.id == sId
                }
                text += listItem?.toShareText(pricePadding = false) + "\n\n"
            }
            copyItemListAttempt.value = Event(text)
        } else copyItemListAttempt.value = Event(item.toShareText())
    }

    private fun shareItemList(pos: Int) = viewModelScope.launch {
        val item = itemListState.value?.get(pos) ?: return@launch
        val deferredSelectionSet by lazyDeferred {
            getSelectionSet()
        }
        if (item.id.isMultipleSelected()) {
            var text = ""
            deferredSelectionSet.await().forEach { sId ->
                val listItem = itemListState.value?.single {
                    it.id == sId
                }
                text += listItem?.toShareText(pricePadding = false) + "\n\n"
            }
            shareItemListAttempt.value = Event(text)
        } else shareItemListAttempt.value = Event(item.toShareText())
    }

    private fun clearListCacheTime() = viewModelScope.launch {
        showLoading()
        val result = itemRepo.clearListCacheTime()
        if (result is Result.Value) itemListUpdated()
        else showError(R.string.cannot_update_local_entries)
        hideLoading()
    }

    private fun clearStaticsCacheTime() = viewModelScope.launch {
        staticsRepo.clearListCacheTime()
    }
}

private suspend fun MutableList<String>.toItemsPosList(itemList: List<Item>?): MutableList<Int> =
    launchASuspendTaskScope {
        val list = mutableListOf<Int>()
        forEach { sId ->
            val pos = itemList?.indexOfFirst { it.id == sId }
            list.add(pos ?: -1)
        }
        return@launchASuspendTaskScope list
    }