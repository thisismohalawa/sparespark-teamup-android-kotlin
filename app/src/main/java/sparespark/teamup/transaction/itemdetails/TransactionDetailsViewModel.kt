package sparespark.teamup.transaction.itemdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.MAX_ASSET_DIG
import sparespark.teamup.core.MAX_ASSET_PRICE_DIG
import sparespark.teamup.core.MAX_ASSET_TOTAL_PRICE_DIG
import sparespark.teamup.core.base.BaseAdministrationViewModel
import sparespark.teamup.core.getCalendarDateTime
import sparespark.teamup.core.getSystemTimeMillis
import sparespark.teamup.core.internal.getQuantity
import sparespark.teamup.core.internal.getTotal
import sparespark.teamup.core.internal.isValidNumFormated
import sparespark.teamup.core.internal.limitDouble
import sparespark.teamup.core.internal.newTransactionItem
import sparespark.teamup.core.internal.toShareText
import sparespark.teamup.core.isNewStringItem
import sparespark.teamup.core.lazyDeferred
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ClientEntry
import sparespark.teamup.data.model.transaction.Transaction
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.TransactionRepository

class TransactionDetailsViewModel(
    private val itemRepo: TransactionRepository,
    clientRepo: ClientRepository,
    preferenceRepo: PreferenceRepository,
    savedStateHandle: SavedStateHandle
) : BaseAdministrationViewModel<ItemDetailsViewEvent>(
    clientRepo = clientRepo,
    preferenceRepo = preferenceRepo,
    cityRepo = null,
    companyRepo = null,
    productRepo = null,
) {
    internal val tempSwitchState = MutableLiveData<Boolean>()
    internal val sellSwitchState = MutableLiveData<Boolean>()

    internal val reqTotalPriceTextState = MutableLiveData<String>()
    internal val reqQuantityTextState = MutableLiveData<String>()

    internal val clientQueryValidateState = MutableLiveData<Boolean>()
    internal val quantityTextValidateState = MutableLiveData<Unit>()
    internal val priceTextValidateState = MutableLiveData<Unit>()

    internal val shareItemAttempt = MutableLiveData<Event<String>>()
    private val navItemId: String? = savedStateHandle["itemId"]

    private val itemState = MutableLiveData<Transaction>()
    val item: LiveData<Transaction> get() = itemState

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState

    override fun handleEvent(event: ItemDetailsViewEvent) {
        when (event) {
            is ItemDetailsViewEvent.OnStartGetItem -> {
                getTransaction()
                getClientList()
            }

            is ItemDetailsViewEvent.OnTempSwitchCheck -> tempSwitchState.value = event.temp
            is ItemDetailsViewEvent.OnSellSwitchCheck -> sellSwitchState.value = event.sell
            is ItemDetailsViewEvent.OnTotalPriceTxtClick -> getReqTotal(event.price, event.quantity)
            is ItemDetailsViewEvent.OnQuantityTxtClick -> getReqQuantity(event.price, event.total)
            is ItemDetailsViewEvent.OnClientAutoCompleteSelect -> updateClient(event.client)
            is ItemDetailsViewEvent.OnUpdateBtnClick -> updateItem(
                event.price,
                event.quantity,
                event.note
            )
        }
    }

    private fun getTransaction() = viewModelScope.launch {
        showLoading()
        if (navItemId.isNullOrBlank()) {
            itemState.value = newTransactionItem()
            tempSwitchState.value = false
            sellSwitchState.value = true
        } else when (val result = itemRepo.getItemById(navItemId)) {
            is Result.Error -> result.error.message.checkExceptionMsg(
                error = {
                    showError(R.string.error_retrieve_data)
                })

            is Result.Value -> {
                itemState.value = result.value
                tempSwitchState.value = result.value.temp
                sellSwitchState.value = result.value.sell
            }
        }
        hideLoading()
    }

    private fun updateItem(
        price: String, quantity: String, note: String
    ) = viewModelScope.launch {
        itemState.value?.let { cItem ->

            if (!isValidInput(cItem.clientEntry.name, price, quantity)) return@launch

            showLoading()

            val cAsset =
                AssetEntry(price.toDouble().limitDouble(), quantity.toDouble().limitDouble())

            if (cItem.id.isNewStringItem()) {
                cItem.id = getSystemTimeMillis()
                cItem.creationDate = getCalendarDateTime()
            }
            when (val result = itemRepo.updateItem(
                cItem.copy(
                    assetEntry = cAsset,
                    note = note,
                    temp = tempSwitchState.value ?: false,
                    sell = sellSwitchState.value ?: true
                )
            )) {
                is Result.Error -> result.error.message.checkExceptionMsg(error = {
                    showError(R.string.cannot_update_entries)
                })

                is Result.Value -> {
                    if (isSuggestShareEnable() == true)
                        shareItemAttempt.value = Event(
                            cItem.copy(
                                assetEntry = cAsset,
                                note = note
                            ).toShareText()
                        )
                    updatedState.value = Unit
                }
            }
            hideLoading()
        }
    }

    private fun getReqTotal(price: String, quantity: String) {
        if (price.isValidNumFormated(MAX_ASSET_PRICE_DIG) &&
            quantity.isValidNumFormated(MAX_ASSET_DIG)
        ) reqTotalPriceTextState.value =
            getTotal(
                assetPrice = price.toDouble(),
                quantity = quantity.toDouble()
            ).toString()
    }

    private fun getReqQuantity(price: String, total: String) {
        if (price.isValidNumFormated(MAX_ASSET_PRICE_DIG) &&
            total.isValidNumFormated(MAX_ASSET_TOTAL_PRICE_DIG)
        ) reqQuantityTextState.value =
            getQuantity(
                assetPrice = price.toDouble(),
                total = total.toDouble()
            ).toString()
    }

    private fun updateClient(clientName: String) = viewModelScope.launch {
        showLoading()
        val cityDeferred by lazyDeferred {
            getClientCityByName(clientName)
        }
        itemState.value?.clientEntry = ClientEntry(
            name = clientName,
            city = cityDeferred.await(),
            id = ""
        )
        clientQueryValidateState.value = true
        hideLoading()
    }

    private fun isValidInput(
        client: String?, price: String, quantity: String
    ): Boolean {
        fun clientValid() = if (client.isNullOrBlank()) {
            clientQueryValidateState.value = false
            false
        } else true

        fun priceValid() = if (!price.isValidNumFormated(MAX_ASSET_DIG)) {
            priceTextValidateState.value = Unit
            false
        } else true

        fun quanValid() = if (!quantity.isValidNumFormated(MAX_ASSET_DIG)) {
            quantityTextValidateState.value = Unit
            false
        } else true

        return clientValid() && priceValid() && quanValid()
    }
}