package sparespark.teamup.stock.itemlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.StockRepository
import sparespark.teamup.stock.BaseStockListViewModel

class StockListViewModel(
    stockRepo: StockRepository,
    preferenceRepository: PreferenceRepository
) : BaseStockListViewModel<StockListEvent>(
    stockRepo = stockRepo,
    cityRepository = null,
    clientRepository = null,
    companyRepository = null,
    productRepository = null,
    preferenceRepo = preferenceRepository
) {
    internal val sellStockAttempt = MutableLiveData<Event<Unit>>()

    override fun handleViewEvent(event: StockListEvent) {
        when (event) {
            is StockListEvent.OnStartGetStockList -> {
                useStockStaticsState.value = isStaticsEnable()
                getItemList(resultAction = {
                    stockListState.value?.calculateListStatics()
                }, errorAction = {
                    useStockStaticsState.value = false
                })
                updateItemListHintTitle()
            }

            is StockListEvent.OnSellBtnClick -> if ((stockListState.value?.size
                    ?: 0) >= 1
            ) sellStockAttempt.value = Event(Unit)
        }
    }

    private fun updateItemListHintTitle() = viewModelScope.launch {
        val result = stockRepo.getItemListHintTitle()
        if (result is Result.Value) updateItemListHintAttempt.value = result.value
    }
}
