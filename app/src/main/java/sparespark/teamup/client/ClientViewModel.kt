package sparespark.teamup.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.DELAY_NAV_RESTART
import sparespark.teamup.core.DELAY_VIEW_EXPAND
import sparespark.teamup.core.getSystemTimeMillis
import sparespark.teamup.core.isNewIdItem
import sparespark.teamup.core.isPhoneNumberValid
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.city.City
import sparespark.teamup.data.model.client.Client
import sparespark.teamup.data.model.client.LocationEntry
import sparespark.teamup.data.repository.CityRepository
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.preferences.base.BasePreferenceViewModel

class ClientViewModel(
    private val clientRepo: ClientRepository,
    private val cityRepo: CityRepository,
    preferenceRepo: PreferenceRepository
) : BasePreferenceViewModel<ClientEvent>(preferenceRepo) {

    internal val bottomSheetViewState = MutableLiveData<Int>()
    internal val filterClientAttempt = MutableLiveData<Event<String>>()
    internal val dialClientAttempt = MutableLiveData<Event<String>>()
    internal val messageClientAttempt = MutableLiveData<Event<String>>()
    internal val addCityNavigateAttempt = MutableLiveData<Event<Unit>>()

    private val clientState = MutableLiveData<Client>()
    val client: LiveData<Client> get() = clientState

    private val clientListState = MutableLiveData<List<Client>>()
    val clientList: LiveData<List<Client>> get() = clientListState

    private val cityListState = MutableLiveData<List<City>?>()
    val cityList: LiveData<List<City>?> get() = cityListState

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState

    override fun handleEvent(event: ClientEvent) {
        when (event) {
            is ClientEvent.OnStartGetClient -> setupClient(pos = null)
            is ClientEvent.OnListItemClick -> setupClient(pos = event.pos)
            is ClientEvent.GetClientList -> getClientList()
            is ClientEvent.HideBottomSheet -> hideBottomSheet()
            is ClientEvent.OnMenuNavigateClientClick -> navigateClient(event.pos)
            is ClientEvent.OnMenuDialClick -> dialClient(event.pos)
            is ClientEvent.OnMenuMsgClick -> messageClient(event.pos)
            is ClientEvent.OnMenuRefreshClick -> clearListCacheTime()
            is ClientEvent.OnSpinnerCitySelect -> updateLocationEntry(event.pos)
            is ClientEvent.OnUpdateTxtClick -> updateClient(event.name, event.phone)
            is ClientEvent.OnMenuDeleteClick -> deleteClient(event.pos)
        }
    }

    private fun setupClient(pos: Int?) = viewModelScope.launch {
        if (pos == null) clientState.value = Client("", "", "", LocationEntry())
        else clientState.value = clientListState.value?.get(pos)

        delay(DELAY_VIEW_EXPAND)
        expandBottomSheet()
        getCityList()
    }

    private fun getCityList() = viewModelScope.launch {
        when (val result = cityRepo.getCityList()) {
            is Result.Error -> showError(R.string.error_retrieve_citylist)
            is Result.Value -> {
                cityListState.value = result.value
                if (cityListState.value?.isEmpty() == true && isSuggestAddEnable())
                    addCityNavigateAttempt.value = Event(Unit)
            }
        }
    }

    private fun getLocalClients() = viewModelScope.launch {
        val result = clientRepo.getClientList(localOnly = Unit)
        if (result is Result.Value) clientListState.value = result.value.asReversed()
        else showError(R.string.cannot_read_local_data)
    }

    private fun getClientList() = viewModelScope.launch {
        showLoading()
        when (val result = clientRepo.getClientList()) {
            is Result.Error -> result.error.message.actionExceptionMsg(
                offline = {
                    getLocalClients()
                },
                unauthorised = { Unit },
                deactivated = { Unit },
                error = {
                    showError(R.string.error_retrieve_data)
                }
            )

            is Result.Value -> clientListState.value = result.value.asReversed()
        }
        hideLoading()
    }

    private fun updateClient(name: String, phone: String) = viewModelScope.launch {
        clientState.value?.let {
            showLoading()
            if (it.id.isNewIdItem()) it.id = getSystemTimeMillis()

            when (val result = clientRepo.updateClient(
                client = it.copy(
                    name = name, phone = phone
                )
            )) {
                is Result.Error -> result.error.message.checkExceptionMsg(error = {
                    showError(R.string.cannot_update_entries)
                })

                is Result.Value -> clientUpdated()
            }
        }
        hideLoading()
    }

    private fun deleteClient(pos: Int) = viewModelScope.launch {
        showLoading()
        when (val result =
            clientListState.value?.get(pos)?.let { clientRepo.deleteClient(id = it.id) }) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.cannot_update_entries)
            })

            is Result.Value -> clientUpdated()
            null -> showError(R.string.cannot_read_local_data)
        }
        hideLoading()
    }

    private fun clearListCacheTime() = viewModelScope.launch {
        showLoading()
        if (clientRepo.clearListCacheTime() is Result.Value) clientUpdated()
        else showError(R.string.cannot_update_local_entries)
        hideLoading()
    }

    private fun navigateClient(pos: Int) = viewModelScope.launch {
        clientListState.value?.get(pos)?.let {
            if (it.name.isNotBlank()) {
                delay(DELAY_NAV_RESTART)
                filterClientAttempt.value = Event(
                    it.name
                )
            }
        }
    }

    private fun updateLocationEntry(pos: Int) {
        cityListState.value?.get(pos)?.let { city ->
            clientState.value?.locationEntry = LocationEntry(
                cityId = city.id,
                cityName = city.name
            )
        }
    }

    private fun dialClient(pos: Int) {
        clientListState.value?.get(pos)?.let {
            if (isValidPhoneNum(it.phone))
                dialClientAttempt.value = Event(it.phone)
        }
    }

    private fun messageClient(pos: Int) {
        clientListState.value?.get(pos)?.let {
            if (isValidPhoneNum(it.phone) &&
                isValidPhoneNumFormat(it.phone)
            )
                messageClientAttempt.value = Event(it.phone)
        }
    }

    private fun clientUpdated() {
        updatedState.value = Unit
    }

    private fun isValidPhoneNum(phone: String): Boolean = if (!phone.isPhoneNumberValid()) {
        showError(R.string.invalid_phone_num)
        false
    } else true

    private fun isValidPhoneNumFormat(phone: String): Boolean =
        if (!phone.startsWith("+20")) {
            showError(R.string.invalid_phone_num_format)
            false
        } else true

    private fun expandBottomSheet() {
        bottomSheetViewState.value = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideBottomSheet() {
        bottomSheetViewState.value = BottomSheetBehavior.STATE_HIDDEN
    }
}

