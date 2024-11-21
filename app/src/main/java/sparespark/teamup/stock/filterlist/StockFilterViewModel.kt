package sparespark.teamup.stock.filterlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.internal.newStock
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.stock.Stock
import sparespark.teamup.data.repository.CityRepository
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.CompanyRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.ProductRepository
import sparespark.teamup.data.repository.StockRepository
import sparespark.teamup.stock.BaseStockListViewModel


class StockFilterViewModel(
    stockRepo: StockRepository,
    cityRepo: CityRepository,
    clientRepo: ClientRepository,
    companyRepository: CompanyRepository,
    productRepo: ProductRepository,
    preferenceRepository: PreferenceRepository
) : BaseStockListViewModel<StockFilterListEvent>(
    stockRepo = stockRepo,
    cityRepository = cityRepo,
    clientRepository = clientRepo,
    companyRepository = companyRepository,
    productRepository = productRepo,
    preferenceRepo = preferenceRepository
) {
    private val itemState = MutableLiveData<Stock>()
    val item: LiveData<Stock> get() = itemState

    override fun handleViewEvent(event: StockFilterListEvent) {
        when (event) {
            is StockFilterListEvent.OnViewStart -> {
                itemState.value = newStock()
                getCityList()
                getClientList()
                getCompanyList()
                getProductList()
                setupNewDefaultStatics()
            }

            is StockFilterListEvent.OnSearchQueryTextUpdate -> updateSearchDate(event.newText)

            is StockFilterListEvent.OnSpinnerCitySelect ->
                if (event.pos == null) itemState.value?.clientEntry?.city =
                    "" else updateCity(event.pos)

            is StockFilterListEvent.OnSpinnerClientSelect ->
                if (event.pos == null) itemState.value?.clientEntry?.name =
                    "" else updateClient(event.pos)

            is StockFilterListEvent.OnSpinnerCompanySelect ->
                if (event.pos == null) itemState.value?.productEntry?.company =
                    "" else updateCompany(event.pos)

            is StockFilterListEvent.OnSpinnerProductSelect ->
                if (event.pos == null) itemState.value?.productEntry?.name = ""
                else updateProduct(event.pos)

            is StockFilterListEvent.OnFilterBtnClick -> if (itemState.value != newStock()) filterItem()

        }
    }

    private fun filterItem() = viewModelScope.launch {
        showLoading()
        when (val result = itemState.value?.let {
            stockRepo.filterItemList(
                stock = it,
            )
        }) {
            is Result.Error -> result.error.message.checkExceptionMsg(
                error = {
                    showError(R.string.error_retrieve_data)
                })

            is Result.Value -> result.value.let {
                if (it.isEmpty()) showError(R.string.empty)
                stockListState.value = it.asReversed()
                it.calculateListStatics()
            }

            else -> showError(R.string.cannot_read_local_data)
        }
        hideLoading()
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

    private fun updateCompany(pos: Int) {
        companyListState.value?.get(pos)?.let { company ->
            itemState.value?.productEntry?.name = ""
            itemState.value?.productEntry?.company = company.name
        }
    }

    private fun updateProduct(pos: Int) {
        productListState.value?.get(pos)?.let { product ->
            itemState.value?.productEntry?.name = product.name
            itemState.value?.productEntry?.company = ""
        }
    }

}