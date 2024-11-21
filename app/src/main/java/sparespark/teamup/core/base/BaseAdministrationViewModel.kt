package sparespark.teamup.core.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.city.City
import sparespark.teamup.data.model.client.Client
import sparespark.teamup.data.model.company.Company
import sparespark.teamup.data.model.product.Product
import sparespark.teamup.data.repository.CityRepository
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.CompanyRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.ProductRepository

open class BaseAdministrationViewModel<VE>(
    private val cityRepo: CityRepository?,
    private val clientRepo: ClientRepository?,
    private val companyRepo: CompanyRepository?,
    private val productRepo: ProductRepository?,
    private val preferenceRepo: PreferenceRepository?
) : BaseViewModel<VE>() {

    internal val addCityNavigateAttempt = MutableLiveData<Event<Unit>>()
    internal val addClientNavigateAttempt = MutableLiveData<Event<Unit>>()
    internal val addCompanyNavigateAttempt = MutableLiveData<Event<Unit>>()
    internal val addProductNavigateAttempt = MutableLiveData<Event<Unit>>()


    protected val cityListState = MutableLiveData<List<City>>()
    val cityList: LiveData<List<City>> get() = cityListState

    protected val clientListState = MutableLiveData<List<Client>>()
    val clientList: LiveData<List<Client>> get() = clientListState

    protected val companyListState = MutableLiveData<List<Company>>()
    val companyList: LiveData<List<Company>> get() = companyListState

    protected val productListState = MutableLiveData<List<Product>>()
    val productList: LiveData<List<Product>> get() = productListState

    override fun handleEvent(event: VE) = Unit

    private fun isSuggestAddEnable() = preferenceRepo?.getSuggestAddStatus()

    protected fun isSuggestShareEnable() = preferenceRepo?.getSuggestShareStatus()

    protected fun isNoteEnable() = preferenceRepo?.getUseNoteStatus()

    protected fun isStaticsEnable() = preferenceRepo?.getUseStaticsStatus()

    protected fun isBalanceEnable() = preferenceRepo?.getUseBalanceStatus()

    private fun getLocalCityList() = viewModelScope.launch {
        val result = cityRepo?.getCityList(localOnly = Unit)
        if (result is Result.Value) cityListState.value = result.value.asReversed()
        else showError(R.string.cannot_read_local_data)
    }

    private fun getLocalClientList() = viewModelScope.launch {
        val result = clientRepo?.getClientList(localOnly = Unit)
        if (result is Result.Value) clientListState.value = result.value.asReversed()
        else showError(R.string.cannot_read_local_data)
    }

    private fun getLocalCompanyList() = viewModelScope.launch {
        val result = companyRepo?.getCompanyList(localOnly = Unit)
        if (result is Result.Value) companyListState.value = result.value.asReversed()
        else showError(R.string.cannot_read_local_data)
    }

    private fun getLocalProductList() = viewModelScope.launch {
        val result = productRepo?.getProductList(localOnly = Unit)
        if (result is Result.Value) productListState.value = result.value.asReversed()
        else showError(R.string.cannot_read_local_data)
    }

    protected fun getCityList() = viewModelScope.launch {
        if (cityRepo == null) {
            showError(R.string.error_retrieve_citylist)
            return@launch
        }
        showLoading()
        when (val result = cityRepo.getCityList()) {
            is Result.Error -> result.error.message.actionExceptionMsg(
                offline = {
                    getLocalCityList()
                },
                unauthorised = { Unit },
                deactivated = { Unit },
                error = {
                    showError(R.string.error_retrieve_citylist)
                }
            )

            is Result.Value -> {
                cityListState.value = result.value.asReversed()
                if (cityListState.value?.isEmpty() == true && isSuggestAddEnable() == true) addCityNavigateAttempt.value =
                    Event(Unit)
            }
        }
        hideLoading()
    }

    protected fun getClientList() = viewModelScope.launch {
        if (clientRepo == null) {
            showError(R.string.error_retrieve_clientlist)
            return@launch
        }
        showLoading()
        when (val result = clientRepo.getClientList()) {
            is Result.Error -> result.error.message.actionExceptionMsg(
                offline = {
                    getLocalClientList()
                },
                unauthorised = { Unit },
                deactivated = { Unit },
                error = {
                    showError(R.string.error_retrieve_data)
                }
            )

            is Result.Value -> {
                clientListState.value = result.value.asReversed()
                if (clientListState.value?.isEmpty() == true && isSuggestAddEnable() == true) addClientNavigateAttempt.value =
                    Event(Unit)
            }
        }
        hideLoading()
    }

    protected fun getCompanyList() = viewModelScope.launch {
        if (companyRepo == null) {
            showError(R.string.error_retrieve_clientlist)
            return@launch
        }
        showLoading()
        when (val result = companyRepo.getCompanyList()) {
            is Result.Error -> result.error.message.actionExceptionMsg(
                offline = {
                    getLocalCompanyList()
                },
                unauthorised = { Unit },
                deactivated = { Unit },
                error = {
                    showError(R.string.error_retrieve_data)
                }
            )

            is Result.Value -> {
                companyListState.value = result.value.asReversed()
                if (companyListState.value?.isEmpty() == true && isSuggestAddEnable() == true) addCompanyNavigateAttempt.value =
                    Event(Unit)
            }
        }
        hideLoading()
    }

    protected fun getProductList() = viewModelScope.launch {
        if (productRepo == null) {
            showError(R.string.error_retrieve_productlist)
            return@launch
        }
        showLoading()
        when (val result = productRepo.getProductList()) {
            is Result.Error -> result.error.message.actionExceptionMsg(
                offline = {
                    getLocalProductList()
                },
                unauthorised = { Unit },
                deactivated = { Unit },
                error = {
                    showError(R.string.error_retrieve_citylist)
                }
            )

            is Result.Value -> {
                productListState.value = result.value.asReversed()
                if (productListState.value?.isEmpty() == true && isSuggestAddEnable() == true) addProductNavigateAttempt.value =
                    Event(Unit)
            }
        }
        hideLoading()
    }

    protected suspend fun getClientCityByName(clientName: String): String {
        return when (val result = clientRepo?.getClientCityByName(clientName)) {
            is Result.Value -> result.value
            is Result.Error -> {
                showError(R.string.cannot_read_local_data)
                ""
            }

            else -> {
                showError(R.string.cannot_read_local_data)
                ""
            }
        }
    }
}