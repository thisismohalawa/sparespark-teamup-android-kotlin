package sparespark.teamup.city

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
import sparespark.teamup.core.wrapper.BaseViewModel
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.city.City
import sparespark.teamup.data.repository.CityRepository

class CityViewModel(
    private val cityRepo: CityRepository
) : BaseViewModel<CityEvent>() {

    internal val bottomSheetViewState = MutableLiveData<Int>()
    internal val filterCityAttempt = MutableLiveData<Event<String>>()

    private val cityState = MutableLiveData<City>()
    val city: LiveData<City> get() = cityState

    private val cityListState = MutableLiveData<List<City>>()
    val cityList: LiveData<List<City>> get() = cityListState

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState

    override fun handleEvent(event: CityEvent) {
        when (event) {
            is CityEvent.OnStartGetCity -> setupCity(pos = null)
            is CityEvent.OnListItemClick -> setupCity(pos = event.pos)
            is CityEvent.GetCityList -> getCityList()
            is CityEvent.HideBottomSheet -> hideBottomSheet()
            is CityEvent.OnMenuNavigateCityClick -> navigateCity(pos = event.pos)
            is CityEvent.OnMenuRefreshClick -> clearListCacheTime()
            is CityEvent.OnMenuDeleteClick -> deleteCity(event.pos)
            is CityEvent.OnUpdateTxtClick -> updateCity(event.city)
        }
    }

    private fun setupCity(pos: Int?) = viewModelScope.launch {
        if (pos == null) cityState.value = City("", "")
        else cityState.value = cityListState.value?.get(pos)
        delay(DELAY_VIEW_EXPAND)
        expandBottomSheet()
    }

    private fun getLocalList() = viewModelScope.launch {
        val result = cityRepo.getCityList(localOnly = Unit)
        if (result is Result.Value) cityListState.value = result.value.asReversed()
        else showError(R.string.cannot_read_local_data)
    }

    private fun getCityList() = viewModelScope.launch {
        showLoading()
        when (val result = cityRepo.getCityList()) {
            is Result.Error -> result.error.message.actionExceptionMsg(
                offline = {
                    getLocalList()
                },
                unauthorised = { Unit },
                deactivated = { Unit },
                error = {
                    showError(R.string.error_retrieve_data)
                }
            )

            is Result.Value -> cityListState.value = result.value.asReversed()
        }
        hideLoading()
    }

    private fun clearListCacheTime() = viewModelScope.launch {
        showLoading()
        if (cityRepo.clearListCacheTime() is Result.Value) cityUpdated()
        else showError(R.string.cannot_update_local_entries)
        hideLoading()
    }

    private fun updateCity(cityName: String) = viewModelScope.launch {
        cityState.value?.let {
            showLoading()
            if (it.id.isNewIdItem())
                cityState.value?.id = getSystemTimeMillis()

            when (val result = cityRepo.updateCity(
                city = it.copy(name = cityName)
            )) {
                is Result.Error -> result.error.message.checkExceptionMsg(error = {
                    showError(R.string.cannot_update_entries)
                })

                is Result.Value -> cityUpdated()
            }
        }
        hideLoading()
    }

    private fun deleteCity(pos: Int) = viewModelScope.launch {
        showLoading()
        when (val result = cityListState.value?.get(pos)?.let { cityRepo.deleteCity(id = it.id) }) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.cannot_update_entries)
            })

            is Result.Value -> cityUpdated()
            null -> showError(R.string.cannot_read_local_data)
        }
        hideLoading()
    }

    private fun navigateCity(pos: Int) = viewModelScope.launch {
        cityListState.value?.get(pos)?.let {
            if (it.name.isNotBlank()) {
                delay(DELAY_NAV_RESTART)
                filterCityAttempt.value = Event(
                    it.name
                )
            }
        }
    }

    private fun cityUpdated() {
        updatedState.value = Unit
    }

    private fun expandBottomSheet() {
        bottomSheetViewState.value = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideBottomSheet() {
        bottomSheetViewState.value = BottomSheetBehavior.STATE_HIDDEN
    }
}

