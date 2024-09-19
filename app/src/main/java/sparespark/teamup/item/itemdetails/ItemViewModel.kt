package sparespark.teamup.item.itemdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.MAX_ASSET_DIG
import sparespark.teamup.core.MAX_TOTAL_PRICE_DIG
import sparespark.teamup.core.getCalendarDateTime
import sparespark.teamup.core.getQuantity
import sparespark.teamup.core.getSystemTimeMillis
import sparespark.teamup.core.getTotal
import sparespark.teamup.core.isNewIdItem
import sparespark.teamup.core.isValidNumFormated
import sparespark.teamup.core.lazyDeferred
import sparespark.teamup.core.limitDouble
import sparespark.teamup.core.map.DEF_ITEM_ACTIVE
import sparespark.teamup.core.map.DEF_ITEM_ADMIN_CRUD
import sparespark.teamup.core.map.DEF_ITEM_SELL
import sparespark.teamup.core.toShareText
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.client.Client
import sparespark.teamup.data.model.item.AssetEntry
import sparespark.teamup.data.model.item.ClientEntry
import sparespark.teamup.data.model.item.Item
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.ItemRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.preferences.base.BasePreferenceViewModel

class ItemViewModel(
    private val itemRepo: ItemRepository,
    private val clientRepo: ClientRepository,
    preferenceRepo: PreferenceRepository,
    savedStateHandle: SavedStateHandle
) : BasePreferenceViewModel<ItemViewEvent>(preferenceRepo) {

    internal val adminSwitchState = MutableLiveData<Boolean>()
    internal val sellSwitchState = MutableLiveData<Boolean>()
    internal val clientEditableState = MutableLiveData<Boolean>()
    internal val shareItemAttempt = MutableLiveData<Event<String>>()
    internal val addNewClientAttempt = MutableLiveData<Event<Unit>>()
    internal val reqTotalPriceTextState = MutableLiveData<String>()
    internal val reqQuantityTextState = MutableLiveData<String>()
    internal val priceTextValidateState = MutableLiveData<Unit>()
    internal val quantityTextValidateState = MutableLiveData<Unit>()

    private val navItemId: String? = savedStateHandle["itemId"]

    private val itemState = MutableLiveData<Item>()
    val item: LiveData<Item> get() = itemState

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState

    private val clientListState = MutableLiveData<List<Client>?>()
    val clientList: LiveData<List<Client>?> get() = clientListState

    override fun handleEvent(event: ItemViewEvent) {
        when (event) {
            is ItemViewEvent.OnStartGetItem -> getItem()

            is ItemViewEvent.OnAdminSwitchCheck -> adminSwitchState.value = event.admin

            is ItemViewEvent.OnSellSwitchCheck -> sellSwitchState.value = event.sell

            is ItemViewEvent.OnTotalPriceTxtClick -> getReqTotal(event.price, event.quantity)

            is ItemViewEvent.OnQuantityTxtClick -> getReqQuantity(event.price, event.total)

            is ItemViewEvent.OnClientAutoCompleteSelect -> updateClient(event.client)

            is ItemViewEvent.OnUpdateBtnClick -> updateItem(event.price, event.quantity, event.note)

        }
    }

    private fun getItem() = viewModelScope.launch {
        showLoading()
        if (navItemId.isNullOrBlank()) {
            itemState.value = newItem()
            updateItemStates(DEF_ITEM_ADMIN_CRUD, DEF_ITEM_SELL, clientEditable = true)
        } else when (val result = itemRepo.getItemById(navItemId)) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.error_retrieve_data)
            })

            is Result.Value -> {
                itemState.value = result.value
                itemState.value?.let {
                    updateItemStates(
                        it.onlyAdmins, it.sell, it.clientEntry.name.isNullOrBlank()
                    )
                }
            }
        }

        hideLoading()
        checkIfClientNeeded()
    }

    private fun updateItemStates(admin: Boolean, sale: Boolean, clientEditable: Boolean) {
        adminSwitchState.value = admin
        sellSwitchState.value = sale
        clientEditableState.value = clientEditable
    }

    private fun getClientList() = viewModelScope.launch {
        showLoading()
        when (val result = clientRepo.getClientList()) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.error_retrieve_clientlist)
            })

            is Result.Value -> result.value.let {
                clientListState.value = it
                if (it.isEmpty() && isSuggestAddEnable()) addNewClientAttempt.value = Event(Unit)
            }
        }
        hideLoading()
    }

    private fun updateClient(clientName: String) = viewModelScope.launch {
        showLoading()
        val idDeferred by lazyDeferred {
            when (val result = clientRepo.getClientIdByName(clientName)) {
                is Result.Value -> result.value
                is Result.Error -> {
                    showError(R.string.cannot_read_local_data)
                    ""
                }
            }
        }
        val cityDeferred by lazyDeferred {
            when (val result = clientRepo.getClientCityByName(clientName)) {
                is Result.Value -> result.value
                is Result.Error -> {
                    showError(R.string.cannot_read_local_data)
                    ""
                }
            }
        }
        itemState.value?.clientEntry = ClientEntry(
            name = clientName, id = idDeferred.await(), city = cityDeferred.await()
        )
        hideLoading()
    }

    private fun updateItem(
        price: String, quantity: String, note: String
    ) = viewModelScope.launch {
        if (isValidInput(price, quantity))
            itemState.value?.let { cItem ->
                showLoading()

                val cAsset =
                    AssetEntry(price.toDouble().limitDouble(), quantity.toDouble().limitDouble())

                if (cItem.id.isNewIdItem()) {
                    cItem.id = getSystemTimeMillis()
                    cItem.creationDate = getCalendarDateTime()
                }

                when (val result = itemRepo.updateItem(
                    cItem.copy(
                        assetEntry = cAsset,
                        note = note,
                        onlyAdmins = adminSwitchState.value ?: DEF_ITEM_ADMIN_CRUD,
                        sell = sellSwitchState.value ?: DEF_ITEM_SELL
                    )
                )) {
                    is Result.Error -> result.error.message.checkExceptionMsg(error = {
                        showError(R.string.cannot_update_entries)
                    })

                    is Result.Value -> {
                        if (isSuggestShareEnable()) suggestShareItem(cAsset)
                        updatedState.value = Unit
                    }
                }
            }
        hideLoading()
    }

    private fun getReqTotal(price: String, quantity: String) {
        if (price.isValidNumFormated(MAX_ASSET_DIG) && quantity.isValidNumFormated(MAX_ASSET_DIG)) reqTotalPriceTextState.value =
            getTotal(
                assetPrice = price.toDouble(), quantity = quantity.toDouble()
            ).toString()
    }

    private fun getReqQuantity(
        price: String, total: String
    ) = viewModelScope.launch {
        if (price.isValidNumFormated(MAX_ASSET_DIG) && total.isValidNumFormated(MAX_TOTAL_PRICE_DIG)) reqQuantityTextState.value =
            getQuantity(
                assetPrice = price.toDouble(), total = total.toDouble()
            ).toString()
    }

    private fun checkIfClientNeeded() {
        if (clientEditableState.value == true) getClientList()
    }

    private fun suggestShareItem(newAssetEntry: AssetEntry) {
        itemState.value?.let {
            shareItemAttempt.value = Event(
                Item(
                    id = "",
                    creationDate = it.creationDate,
                    clientEntry = ClientEntry(
                        it.clientEntry.id, it.clientEntry.name
                    ),
                    assetEntry = newAssetEntry,
                    note = it.note,
                    onlyAdmins = adminSwitchState.value ?: DEF_ITEM_ADMIN_CRUD,
                    sell = sellSwitchState.value ?: DEF_ITEM_SELL,
                    active = it.active,
                    updateDate = "",
                    updateBy = ""
                ).toShareText()
            )
        }
    }


    private fun isValidInput(price: String, quantity: String): Boolean {
        fun priceValid() = if (!price.isValidNumFormated(MAX_ASSET_DIG)) {
            priceTextValidateState.value = Unit
            false
        } else true

        fun quanValid() = if (!quantity.isValidNumFormated(MAX_ASSET_DIG)) {
            quantityTextValidateState.value = Unit
            false
        } else true

        return priceValid() && quanValid()
    }
}

private fun newItem() = Item(
    "",
    "",
    ClientEntry(),
    AssetEntry(),
    active = DEF_ITEM_ACTIVE,
    onlyAdmins = DEF_ITEM_ADMIN_CRUD,
    sell = DEF_ITEM_SELL,
    note = "",
    updateBy = "",
    updateDate = ""
)