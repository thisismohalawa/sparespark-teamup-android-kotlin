package sparespark.teamup.transaction.filterlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.internal.newTransactionItem
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.balance.TransactionBalance
import sparespark.teamup.data.model.transaction.Transaction
import sparespark.teamup.data.repository.CityRepository
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.TransactionRepository
import sparespark.teamup.transaction.BaseTransactionListViewModel

class TransactionFilterListViewModel(
    transactionRepository: TransactionRepository,
    cityRepository: CityRepository?,
    clientRepository: ClientRepository?,
    preferenceRepository: PreferenceRepository?,
    savedStateHandle: SavedStateHandle
) : BaseTransactionListViewModel<TransactionFilterListEvent, Nothing>(
    transactionRepository = transactionRepository,
    cityRepository = cityRepository,
    clientRepository = clientRepository,
    preferenceRepository = preferenceRepository
) {
    internal val searchQueryTextState = MutableLiveData<String>()

    private val navQuery: String? = savedStateHandle["search_query"]

    private val itemState = MutableLiveData<Transaction>()
    val item: LiveData<Transaction> get() = itemState

    override fun handleViewEvent(event: TransactionFilterListEvent) {
        when (event) {
            is TransactionFilterListEvent.OnViewStart -> {
                useBalanceState.value = isBalanceEnable()
                itemState.value = newTransactionItem()
                updateItemListHintTitle()
                getCityList()
                getClientList()
                checkIfHasQueryArgument()
                setupNewDefaultBalance()
            }

            is TransactionFilterListEvent.OnSearchQueryTextUpdate -> updateSearchDate(event.newText)


            is TransactionFilterListEvent.OnSpinnerCitySelect -> if (event.pos == null) itemState.value?.clientEntry?.city =
                ""
            else updateCity(event.pos)

            is TransactionFilterListEvent.OnSpinnerClientSelect -> if (event.pos == null) itemState.value?.clientEntry?.name =
                ""
            else updateClient(event.pos)

            is TransactionFilterListEvent.OnTransactionFilterBtnClick ->
                if (itemState.value != newTransactionItem()) filterItem()

        }
    }

    private fun updateItemListHintTitle() = viewModelScope.launch {
        val result = transactionRepository.getFilteredItemListHintTitle()
        if (result is Result.Value) updateItemListHintAttempt.value = result.value
    }

    private fun List<Transaction>.calculateListStatics() = viewModelScope.launch {
        if (useBalanceState.value == false) return@launch
        val result = transactionRepository.calculateBalance(list = this@calculateListStatics)
        if (result is Result.Value) balanceListState.value = result.value
        else showError(R.string.cannot_read_statistics)
    }

    private fun updateSearchDate(date: String) {
        itemState.value?.creationDate = date
    }

    private fun updateCity(pos: Int) {
        cityListState.value?.get(pos)?.let { city ->
            itemState.value?.clientEntry?.city = city.name
            itemState.value?.clientEntry?.name = ""
        }
    }

    private fun updateClient(pos: Int) {
        clientListState.value?.get(pos)?.let { client ->
            itemState.value?.clientEntry?.name = client.name
            itemState.value?.clientEntry?.city = ""
        }
    }

    private fun checkIfHasQueryArgument() {
        navQuery?.let {
            searchQueryTextState.value = it
        }
    }

    private fun setupNewDefaultBalance() {
        if (useBalanceState.value == false) return
        balanceListState.value = listOf(
            TransactionBalance(
                0,
                0.0,
                isSell = true,
                desRes = R.string.cost
            )
        )
    }

    private fun filterItem() = viewModelScope.launch {
        showLoading()
        when (val result = itemState.value?.let {
            transactionRepository.filterItemList(
                item = it,
            )
        }) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.error_retrieve_data)
            })

            is Result.Value -> result.value.let {
                if (it.isEmpty()) showError(R.string.empty)
                itemListState.value = it.asReversed()
                it.calculateListStatics()
            }

            else -> showError(R.string.cannot_read_local_data)
        }
        hideLoading()
    }
}