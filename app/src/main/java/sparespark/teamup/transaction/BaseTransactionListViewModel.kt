package sparespark.teamup.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.internal.toShareText
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.exportApi.ExcelAPIImpl
import sparespark.teamup.data.model.balance.TransactionBalance
import sparespark.teamup.data.model.transaction.Transaction
import sparespark.teamup.data.repository.CityRepository
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.TransactionRepository

open class BaseTransactionListViewModel<VE, AVE>(
    protected val transactionRepository: TransactionRepository,
    cityRepository: CityRepository?,
    clientRepository: ClientRepository?,
    preferenceRepository: PreferenceRepository?
) : BaseTransactionListEventViewModel<BaseTransactionListEvent, VE, AVE>(
    cityRepository = cityRepository,
    clientRepository = clientRepository,
    preferenceRepository = preferenceRepository
) {
    internal val updateItemListHintAttempt = MutableLiveData<List<Int>>()
    internal val shareItemListAttempt = MutableLiveData<Event<String?>>()
    internal val copyItemListAttempt = MutableLiveData<Event<String?>>()
    internal val useBalanceState = MutableLiveData<Boolean>()

    protected val itemListState = MutableLiveData<List<Transaction>>()
    val itemList: LiveData<List<Transaction>> get() = itemListState

    protected val balanceListState = MutableLiveData<List<TransactionBalance>>()
    val balanceList: LiveData<List<TransactionBalance>> get() = balanceListState

    private val editItemState = MutableLiveData<Event<String>>()
    val editItem: LiveData<Event<String>> get() = editItemState

    private val updateState = MutableLiveData<Event<Unit>>()
    val updated: LiveData<Event<Unit>> get() = updateState

    private val exportState = MutableLiveData<Unit>()
    val exported: LiveData<Unit> get() = exportState

    override fun handleViewEvent(event: VE) = Unit

    override fun handleAttachViewEvent(event: AVE) = Unit

    override fun handleEvent(event: BaseTransactionListEvent) {
        when (event) {
            is BaseTransactionListEvent.OnMenuItemListRefresh -> clearListCacheTime()
            is BaseTransactionListEvent.OnMenuItemUpdateClick -> editItem(event.pos)
            is BaseTransactionListEvent.OnItemListLongClick -> onItemListLongViewClick(event.pos)
            is BaseTransactionListEvent.OnMenuItemListPushClick -> pushItem(event.pos)
            is BaseTransactionListEvent.OnMenuItemListDeleteClick -> deleteItem(event.pos)
            is BaseTransactionListEvent.OnMenuItemListCopyClick -> copyItem(event.pos)
            is BaseTransactionListEvent.OnMenuItemListShareClick -> shareItem(event.pos)
            is BaseTransactionListEvent.OnMenuItemListExportClick -> exportList()
            is BaseTransactionListEvent.OnItemListCheckboxClick -> {
                activateItem(event.pos, event.isActive)
            }
        }
    }

    private fun getLocalList() = viewModelScope.launch {
        val result = transactionRepository.getItemList(localOnly = Unit)
        if (result is Result.Value) itemListState.value = result.value
        else showError(R.string.error_retrieve_data)
    }

    protected fun getItemList(
        errorAction: (() -> Unit)? = null,
        resultAction: (() -> Unit)? = null
    ) = viewModelScope.launch {
        showLoading()
        when (val result = transactionRepository.getItemList()) {
            is Result.Error -> {
                result.error.message.actionExceptionMsg(offline = {
                    getLocalList()
                }, unauthorised = {
                    showError(R.string.unauthorized)
                }, deactivated = {
                    showError(R.string.deactivated)
                }, error = {
                    showError(R.string.error_retrieve_itemlist)
                })
                errorAction?.invoke()
            }

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

        val result = when (isMultipleSelectionList(itemId = id)) {
            true -> transactionRepository.activateItem(
                itemsIds = getSelectionList(),
                isActive = active
            )

            false -> transactionRepository.activateItem(
                itemId = id,
                isActive = active
            )
        }

        if (result is Result.Error) result.error.message.checkExceptionMsg(error = { showError(R.string.cannot_update_entries) })
        else itemListUpdated()

        hideLoading()
    }

    private fun pushItem(pos: Int) = viewModelScope.launch {
        val id = itemListState.value?.get(pos)?.id
        if (id.isNullOrBlank()) return@launch

        showLoading()

        val result = when (isMultipleSelectionList(itemId = id)) {
            true -> transactionRepository.pushItem(itemsIds = getSelectionList())

            false -> transactionRepository.pushItem(itemId = id)
        }

        if (result is Result.Error) result.error.message.checkExceptionMsg(error = { showError(R.string.cannot_update_entries) })
        else itemListUpdated()
        hideLoading()
    }

    private fun deleteItem(pos: Int) = viewModelScope.launch {
        val id = itemListState.value?.get(pos)?.id
        if (id.isNullOrBlank()) return@launch

        showLoading()

        val result = when (isMultipleSelectionList(itemId = id)) {
            true -> transactionRepository.deleteItem(itemsIds = getSelectionList())

            false -> transactionRepository.deleteItem(itemId = id)
        }

        if (result is Result.Error) result.error.message.checkExceptionMsg(error = { showError(R.string.cannot_update_entries) }) else itemListUpdated()
        hideLoading()
    }

    private fun copyItem(pos: Int) = viewModelScope.launch {
        itemListState.value?.get(pos)?.let {

            if (isMultipleSelectionList(itemId = it.id)) {
                var text = ""
                getSelectionList().forEach { sId ->
                    val listItem = itemListState.value?.single { traget ->
                        traget.id == sId
                    }
                    text += listItem?.toShareText(pricePadding = false) + "\n\n"
                }
                copyItemListAttempt.value = Event(text)
            } else copyItemListAttempt.value = Event(it.toShareText())
        }
    }

    private fun shareItem(pos: Int) = viewModelScope.launch {
        itemListState.value?.get(pos)?.let {

            if (isMultipleSelectionList(itemId = it.id)) {
                var text = ""
                getSelectionList().forEach { sId ->
                    val listItem = itemListState.value?.single { traget ->
                        traget.id == sId
                    }
                    text += listItem?.toShareText(pricePadding = false) + "\n\n"
                }
                shareItemListAttempt.value = Event(text)
            } else shareItemListAttempt.value = Event(it.toShareText())
        }
    }

    private fun onItemListLongViewClick(pos: Int) = viewModelScope.launch {
        val itemId = itemListState.value?.get(pos)?.id
        itemId?.let { updateSelection(itemId = it, itemPos = pos) }
    }

    private fun exportList() = viewModelScope.launch {
        showLoading()
        when (itemListState.value?.let {
            ExcelAPIImpl().buildTransactionListFile(
                list = it
            )
        }) {
            is Result.Error -> showError(R.string.error_data_backup)

            is Result.Value -> exportState.value = Unit

            null -> showError(R.string.cannot_read_local_data)
        }
        hideLoading()
    }

    private fun clearListCacheTime() = viewModelScope.launch {
        showLoading()
        val result = transactionRepository.clearListCacheTime()
        if (result is Result.Value) itemListUpdated()
        else showError(R.string.cannot_update_local_entries)
        hideLoading()
    }

    private fun editItem(pos: Int) {
        editItemState.value = itemListState.value?.get(pos)?.let { Event(it.id) }
    }

    private fun itemListUpdated() {
        updateState.value = Event(Unit)
    }
}