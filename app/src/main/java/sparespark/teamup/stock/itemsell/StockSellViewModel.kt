package sparespark.teamup.stock.itemsell

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.MAX_ASSET_DIG
import sparespark.teamup.core.getCalendarDateTime
import sparespark.teamup.core.getSystemTimeMillis
import sparespark.teamup.core.internal.isValidNumFormated
import sparespark.teamup.core.internal.limitDouble
import sparespark.teamup.core.internal.newStock
import sparespark.teamup.core.internal.toShareText
import sparespark.teamup.core.lazyDeferred
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ClientEntry
import sparespark.teamup.data.model.stock.Stock
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.StockRepository
import sparespark.teamup.stock.BaseStockSellViewModel

class StockSellViewModel(
    stockRepo: StockRepository,
    clientRepo: ClientRepository,
    preferenceRepo: PreferenceRepository
) : BaseStockSellViewModel<StockSellViewEvent>(
    stockRepo = stockRepo,
    clientRepo = clientRepo,
    preferenceRepo = preferenceRepo,
) {
    internal val tempSwitchState = MutableLiveData<Boolean>()

    internal val clientQueryValidateState = MutableLiveData<Boolean>()

    internal val quantityTextValidateState = MutableLiveData<Unit>()

    internal val shareItemAttempt = MutableLiveData<Event<String>>()

    private val assetSellQuantityState = MutableLiveData<Double>()
    val assetSellQuantity: LiveData<Double> get() = assetSellQuantityState

    private val stockState = MutableLiveData<Stock>()
    val stock: LiveData<Stock> get() = stockState

    private val sellState = MutableLiveData<Unit>()
    val sell: LiveData<Unit> get() = sellState

    override fun handleEvent(event: StockSellViewEvent) {
        when (event) {
            is StockSellViewEvent.OnStartGetItem -> {
                stockState.value = newStock(isSell = true)
                getStockList(
                    emptyListAction = {
                        showError(R.string.empty)

                    },
                    resultAction = {
                        stockListState.value = it.distinctBy {
                            Pair(it.productEntry.name, it.productEntry.company)
                        }
                    })
                getClientList()
            }

            is StockSellViewEvent.OnTempSwitchCheck -> tempSwitchState.value = event.temp

            is StockSellViewEvent.OnClientAutoCompleteSelect -> updateClient(event.client)

            is StockSellViewEvent.OnSpinnerStockProductSelect -> {
                updateStockProduct(event.pos)
                getAvailableAssetQuantityList(stockState.value, emptyListAction = {
                    showError(R.string.empty)
                    assetSellQuantityState.value = 0.0
                }, resultAction = { maxQuantity ->
                    assetSellQuantityState.value = maxQuantity
                })
            }

            is StockSellViewEvent.OnUpdateBtnClick -> sellItem(event.quantity, event.note)
        }
    }

    private fun sellItem(
        quantity: String,
        note: String
    ) = viewModelScope.launch {
        stockState.value?.let { cItem ->

            if (!isValidInput(client = cItem.clientEntry.name, quantity = quantity)) return@launch

            val cAsset =
                AssetEntry(0.0, quantity.toDouble().limitDouble())

            assetSellQuantityState.value?.let {
                if (cAsset.quantity == 0.0 ||
                    cAsset.quantity!! > it
                ) {
                    quantityTextValidateState.value = Unit
                    return@launch
                }
            }

            showLoading()

            cItem.id = getSystemTimeMillis()
            cItem.creationDate = getCalendarDateTime()

            when (val result = stockRepo.updateItem(
                cItem.copy(
                    assetEntry = cAsset,
                    temp = tempSwitchState.value ?: false,
                    note = note
                )
            )) {
                is Result.Error -> result.error.message.checkExceptionMsg(error = {
                    showError(R.string.cannot_update_entries)
                })

                is Result.Value -> {
                    if (isSuggestShareEnable() == true) shareItemAttempt.value = Event(
                        cItem.copy(
                            note = note,
                            assetEntry = cAsset,
                        ).toShareText()
                    )
                    sellState.value = Unit
                }
            }
            hideLoading()
        }
    }

    private fun updateStockProduct(pos: Int) {
        stockListState.value?.get(pos)?.let {
            stockState.value?.productEntry?.name = it.productEntry.name
            stockState.value?.productEntry?.company = it.productEntry.company
        }
    }

    private fun updateClient(clientName: String) = viewModelScope.launch {
        showLoading()
        val cityDeferred by lazyDeferred {
            getClientCityByName(clientName)
        }
        stockState.value?.clientEntry = ClientEntry(
            name = clientName,
            city = cityDeferred.await(),
            id = ""
        )
        clientQueryValidateState.value = true
        hideLoading()
    }

    private fun isValidInput(
        client: String?, quantity: String
    ): Boolean {
        fun clientValid() = if (client.isNullOrBlank()) {
            clientQueryValidateState.value = false
            false
        } else true


        fun quanValid() = if (!quantity.isValidNumFormated(maxDig = MAX_ASSET_DIG)) {
            quantityTextValidateState.value = Unit
            false
        } else true

        return clientValid() && quanValid()
    }
}