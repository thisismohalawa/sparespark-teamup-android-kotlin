package sparespark.teamup.stock.itemdetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.MAX_ASSET_DIG
import sparespark.teamup.core.base.BaseAdministrationViewModel
import sparespark.teamup.core.getCalendarDateTime
import sparespark.teamup.core.getSystemTimeMillis
import sparespark.teamup.core.internal.isValidNumFormated
import sparespark.teamup.core.internal.limitDouble
import sparespark.teamup.core.internal.newStock
import sparespark.teamup.core.internal.toShareText
import sparespark.teamup.core.isNewStringItem
import sparespark.teamup.core.lazyDeferred
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ProductEntry
import sparespark.teamup.data.model.stock.Stock
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.ProductRepository
import sparespark.teamup.data.repository.StockRepository

class StockDetailsViewModel(
    private val stockRepo: StockRepository,
    productRepo: ProductRepository,
    preferenceRepo: PreferenceRepository,
    savedStateHandle: SavedStateHandle
) : BaseAdministrationViewModel<StockDetailsViewEvent>(
    productRepo = productRepo,
    preferenceRepo = preferenceRepo,
    cityRepo = null,
    clientRepo = null,
    companyRepo = null
) {
    internal val tempSwitchState = MutableLiveData<Boolean>()

    internal val productQueryValidateState = MutableLiveData<Boolean>()
    internal val quantityTextValidateState = MutableLiveData<Unit>()


    internal val shareItemAttempt = MutableLiveData<Event<String>>()

    private val navItemId: String? = savedStateHandle["itemId"]

    private val stockState = MutableLiveData<Stock>()
    val stock: LiveData<Stock> get() = stockState

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState

    override fun handleEvent(event: StockDetailsViewEvent) {
        when (event) {
            is StockDetailsViewEvent.OnStartGetItem -> {
                getItem()
                getProductList()
            }

            is StockDetailsViewEvent.OnStockAutoCompleteSelect -> updateProductEntry(event.productCompany)

            is StockDetailsViewEvent.OnTempSwitchCheck -> tempSwitchState.value = event.temp
            is StockDetailsViewEvent.OnUpdateBtnClick -> updateItem(event.quantity, event.note)
        }
    }

    private fun getItem() = viewModelScope.launch {
        showLoading()
        if (navItemId.isNullOrBlank()) {
            stockState.value = newStock()
        } else when (val result = stockRepo.getItemById(navItemId)) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.error_retrieve_data)
            })

            is Result.Value -> stockState.value = result.value
        }

        hideLoading()
    }

    private fun updateItem(quantity: String, note: String) = viewModelScope.launch {
        stockState.value?.let { cItem ->

            if (!isValidInput(cItem.productEntry.name, quantity)) return@launch

            showLoading()

            val cAsset =
                AssetEntry(0.0, quantity.toDouble().limitDouble())

            if (cItem.id.isNewStringItem()) {
                cItem.id = getSystemTimeMillis()
                cItem.creationDate = getCalendarDateTime()
            }

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
                    updatedState.value = Unit
                }

            }
            hideLoading()
        }
    }

    private fun updateProductEntry(productCompany: String) = viewModelScope.launch {
        showLoading()

        val product = productCompany.replaceAfter("\\", "").dropLast(1)
        val company = productCompany.substring(productCompany.lastIndexOf("\\")).drop(1)

        stockState.value?.productEntry = ProductEntry(
            name = product,
            company = company,
            id = ""
        )

        productQueryValidateState.value = true
        hideLoading()
    }

    private fun isValidInput(
        product: String?,
        quantity: String
    ): Boolean {
        fun productValid() = if (product.isNullOrBlank()) {
            productQueryValidateState.value = false
            false
        } else true

        fun quanValid() = if (!quantity.isValidNumFormated(MAX_ASSET_DIG)) {
            quantityTextValidateState.value = Unit
            false
        } else true

        return productValid() && quanValid()
    }
}