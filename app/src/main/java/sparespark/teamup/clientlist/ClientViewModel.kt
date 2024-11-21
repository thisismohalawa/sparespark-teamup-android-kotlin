package sparespark.teamup.clientlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.DELAY_VIEW_EXPAND
import sparespark.teamup.core.base.BaseAdministrationViewModel
import sparespark.teamup.core.getSystemTimeMillis
import sparespark.teamup.core.isNewStringItem
import sparespark.teamup.core.isPhoneNumberValid
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.LocationEntry
import sparespark.teamup.data.model.client.Client
import sparespark.teamup.data.repository.CityRepository
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.PreferenceRepository

class ClientViewModel(
    private val clientRepo: ClientRepository,
    cityRepo: CityRepository,
    preferenceRepo: PreferenceRepository
) : BaseAdministrationViewModel<ClientEvent>(
    cityRepo = cityRepo,
    clientRepo = clientRepo,
    preferenceRepo = preferenceRepo,
    companyRepo = null,
    productRepo = null,
) {
    internal val bottomSheetViewState = MutableLiveData<Int>()
    internal val dialClientAttempt = MutableLiveData<Event<String>>()

    private val clientState = MutableLiveData<Client>()
    val client: LiveData<Client> get() = clientState

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState

    override fun handleEvent(event: ClientEvent) {
        when (event) {
            is ClientEvent.OnStartGetClient -> setupClient(pos = null)
            is ClientEvent.OnListItemClick -> setupClient(pos = event.pos)
            is ClientEvent.OnListItemLongClick -> onListItemViewClick(event.pos)
            is ClientEvent.GetClientList -> getClientList()
            is ClientEvent.HideBottomSheet -> hideBottomSheet()
            is ClientEvent.OnMenuDialClick -> dialClient(event.pos)
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

    private fun updateClient(name: String, phone: String) = viewModelScope.launch {
        clientState.value?.let {
            showLoading()
            if (it.id.isNewStringItem()) it.id = getSystemTimeMillis()

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
        val id = clientListState.value?.get(pos)?.id
        if (id.isNullOrBlank()) return@launch

        showLoading()

        val result = when (isMultipleSelectionList(itemId = id)) {
            true -> clientRepo.deleteClient(itemsIds = getSelectionList())

            false -> clientRepo.deleteClient(itemId = id)
        }

        if (result is Result.Error) result.error.message.checkExceptionMsg(error = { showError(R.string.cannot_update_entries) }) else clientUpdated()
        hideLoading()
    }

    private fun clearListCacheTime() = viewModelScope.launch {
        showLoading()
        if (clientRepo.clearListCacheTime() is Result.Value) clientUpdated()
        else showError(R.string.cannot_update_local_entries)
        hideLoading()
    }

    private fun onListItemViewClick(pos: Int) = viewModelScope.launch {
        val itemId = clientListState.value?.get(pos)?.id
        itemId?.let { updateSelection(itemId = it, itemPos = pos) }
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


    private fun isValidPhoneNum(phone: String): Boolean = if (!phone.isPhoneNumberValid()) {
        showError(R.string.invalid_phone_num)
        false
    } else true

    private fun clientUpdated() {
        updatedState.value = Unit
    }

    private fun expandBottomSheet() {
        bottomSheetViewState.value = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideBottomSheet() {
        bottomSheetViewState.value = BottomSheetBehavior.STATE_HIDDEN
    }
}

