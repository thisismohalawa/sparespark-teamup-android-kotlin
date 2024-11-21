package sparespark.teamup.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.internal.toShareText
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.exportApi.ExcelAPIImpl
import sparespark.teamup.data.model.statics.StockStatics
import sparespark.teamup.data.model.stock.Stock
import sparespark.teamup.data.repository.CityRepository
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.CompanyRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.ProductRepository
import sparespark.teamup.data.repository.StockRepository

open class BaseStockListViewModel<VE>(
    protected val stockRepo: StockRepository,
    cityRepository: CityRepository?,
    clientRepository: ClientRepository?,
    companyRepository: CompanyRepository?,
    productRepository: ProductRepository?,
    preferenceRepo: PreferenceRepository?,
) : BaseStockListEventViewModel<BaseStockListEvent, VE>(
    cityRepository = cityRepository,
    clientRepository = clientRepository,
    companyRepository = companyRepository,
    productRepository = productRepository,
    preferenceRepo = preferenceRepo
) {
    internal val updateItemListHintAttempt = MutableLiveData<List<Int>>()
    internal val useStockStaticsState = MutableLiveData<Boolean>()
    internal val shareItemListAttempt = MutableLiveData<Event<String?>>()
    internal val copyItemListAttempt = MutableLiveData<Event<String?>>()

    protected val stockListState = MutableLiveData<List<Stock>>()
    val stockList: LiveData<List<Stock>> get() = stockListState

    protected val stockStaticsListState = MutableLiveData<List<StockStatics>>()
    val stockStaticsList: LiveData<List<StockStatics>> get() = stockStaticsListState

    protected val editStockState = MutableLiveData<Event<String>>()
    val editIStock: LiveData<Event<String>> get() = editStockState

    private val updateState = MutableLiveData<Event<Unit>>()
    val updatedStock: LiveData<Event<Unit>> get() = updateState

    private val exportState = MutableLiveData<Unit>()
    val exported: LiveData<Unit> get() = exportState

    override fun handleViewEvent(event: VE) = Unit

    override fun handleEvent(event: BaseStockListEvent) {
        when (event) {
            is BaseStockListEvent.OnMenuItemRefresh -> clearListCacheTime()
            is BaseStockListEvent.OnListItemLongClick -> onListItemViewClick(event.pos)
            is BaseStockListEvent.OnMenuItemDeleteClick -> deleteItem(event.pos)
            is BaseStockListEvent.OnMenuItemPushClick -> pushItem(event.pos)
            is BaseStockListEvent.OnMenuItemCopyClick -> copyItem(event.pos)
            is BaseStockListEvent.OnMenuItemShareClick -> shareItem(event.pos)
            is BaseStockListEvent.OnMenuItemUpdateClick -> editItem(event.pos)
            is BaseStockListEvent.OnMenuItemExportClick -> exportList()

        }
    }

    protected fun setupNewDefaultStatics() {
        stockStaticsListState.value = listOf(
            StockStatics(
                id = "0",
                product = "ِAa",
                company = "",
                quantity = 0.0
            )
        )
    }

    private fun getLocalList() = viewModelScope.launch {
        val result = stockRepo.getItemList(localOnly = Unit)
        if (result is Result.Value) stockListState.value = result.value
        else showError(R.string.error_retrieve_data)
    }

    protected fun getItemList(
        errorAction: (() -> Unit)? = null,
        resultAction: (() -> Unit)? = null
    ) = viewModelScope.launch {
        showLoading()
        when (val result = stockRepo.getItemList()) {
            is Result.Error -> {
                result.error.message.actionExceptionMsg(offline = {
                    getLocalList()
                }, unauthorised = {
                    showError(R.string.unauthorized)
                }, deactivated = {
                    showError(R.string.deactivated)
                }, error = {
                    showError(R.string.error_retrieve_stocklist)
                })
                errorAction?.invoke()
            }

            is Result.Value -> {
                stockListState.value = result.value.asReversed()
                resultAction?.invoke()
            }
        }
        hideLoading()
    }

    protected fun List<Stock>.calculateListStatics() = viewModelScope.launch {
        if (useStockStaticsState.value == false) return@launch
        when (val result = stockRepo.calculateListStatics(list = this@calculateListStatics)) {
            is Result.Error -> result.error.message.actionExceptionMsg(
                offline = {

                }, unauthorised = {

                }, deactivated = {

                }, error = {
                    showError(R.string.cannot_read_statistics)
                })

            is Result.Value -> {
                stockStaticsListState.value = result.value
                if (stockStaticsListState.value.isNullOrEmpty()) setupNewDefaultStatics()
            }
        }
    }

    private fun pushItem(pos: Int) = viewModelScope.launch {
        stockListState.value?.get(pos)?.id?.let {
            if (it.isBlank()) return@launch

            showLoading()

            val result = when (isMultipleSelectionList(itemId = it)) {
                true -> stockRepo.pushItem(itemsIds = getSelectionList())

                false -> stockRepo.pushItem(itemId = it)
            }

            if (result is Result.Error) result.error.message.checkExceptionMsg(error = { showError(R.string.cannot_update_entries) })
            else itemListUpdated()
            hideLoading()
        }
    }

    private fun deleteItem(pos: Int) = viewModelScope.launch {
        stockListState.value?.get(pos)?.id?.let {
            if (it.isBlank()) return@launch

            showLoading()

            val result = when (isMultipleSelectionList(itemId = it)) {
                true -> stockRepo.deleteItem(itemsIds = getSelectionList())

                false -> stockRepo.deleteItem(itemId = it)
            }

            if (result is Result.Error) result.error.message.checkExceptionMsg(error = { showError(R.string.cannot_update_entries) }) else itemListUpdated()
            hideLoading()
        }
    }

    private fun copyItem(pos: Int) = viewModelScope.launch {
        stockListState.value?.get(pos)?.let {

            if (isMultipleSelectionList(itemId = it.id)) {
                var text = ""
                getSelectionList().forEach { sId ->
                    val listItem = stockListState.value?.single { traget ->
                        traget.id == sId
                    }
                    text += listItem?.toShareText() + "\n\n"
                }
                copyItemListAttempt.value = Event(text)
            } else copyItemListAttempt.value = Event(it.toShareText())

        }
    }

    private fun shareItem(pos: Int) = viewModelScope.launch {
        stockListState.value?.get(pos)?.let {

            if (isMultipleSelectionList(itemId = it.id)) {
                var text = ""
                getSelectionList().forEach { sId ->
                    val listItem = stockListState.value?.single { traget ->
                        traget.id == sId
                    }
                    text += listItem?.toShareText() + "\n\n"
                }
                shareItemListAttempt.value = Event(text)
            } else shareItemListAttempt.value = Event(it.toShareText())

        }
    }


    private fun onListItemViewClick(pos: Int) = viewModelScope.launch {
        val itemId = stockListState.value?.get(pos)?.id
        itemId?.let { updateSelection(itemId = it, itemPos = pos) }
    }

    private fun editItem(position: Int) {
        stockListState.value?.get(position)?.let {
            editStockState.value = Event(it.id)
        }
    }

    private fun exportList() = viewModelScope.launch {
        showLoading()
        when (stockListState.value?.let {
            ExcelAPIImpl().buildStockListFile(
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
        val result = stockRepo.clearListCacheTime()
        if (result is Result.Value) itemListUpdated()
        else showError(R.string.cannot_update_local_entries)
        hideLoading()
    }

    private fun itemListUpdated() {
        updateState.value = Event(Unit)
    }
}