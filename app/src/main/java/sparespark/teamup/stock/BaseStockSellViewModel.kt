package sparespark.teamup.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.core.base.BaseAdministrationViewModel
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.stock.Stock
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.StockRepository
import sparespark.teamup.R

open class BaseStockSellViewModel<VE>(
    protected val stockRepo: StockRepository,
    clientRepo: ClientRepository?,
    preferenceRepo: PreferenceRepository?
) : BaseAdministrationViewModel<VE>(
    clientRepo = clientRepo,
    preferenceRepo = preferenceRepo,
    cityRepo = null,
    companyRepo = null,
    productRepo = null
) {

    protected val stockListState = MutableLiveData<List<Stock>>()
    val stockList: LiveData<List<Stock>> get() = stockListState

    protected fun getStockList(
        emptyListAction: (() -> Unit)? = null,
        resultAction: ((List<Stock>) -> Unit)? = null,
    ) = viewModelScope.launch {
        showLoading()
        when (val result = stockRepo.getItemList()) {
            is Result.Error -> {
                showError(R.string.error_retrieve_stocklist)
                emptyListAction?.invoke()
            }

            is Result.Value -> if (result.value.isNotEmpty()) resultAction?.invoke(result.value)
            else emptyListAction?.invoke()
        }
        hideLoading()
    }

    protected fun getAvailableAssetQuantityList(
        stock: Stock?,
        emptyListAction: (() -> Unit)? = null,
        resultAction: ((Double) -> Unit)? = null
    ) = viewModelScope.launch {
        showLoading()
        when (val result = stock?.let { stockRepo.getAvailableAssetQuantity(stock = it) }) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.error_retrieve_data)
                emptyListAction?.invoke()
            })

            is Result.Value -> if (result.value != 0.0) resultAction?.invoke(result.value)
            else emptyListAction?.invoke()

            null -> {
                showError(R.string.cannot_read_local_data)
                emptyListAction?.invoke()
            }
        }
        hideLoading()
    }

}