package sparespark.teamup.citylist

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
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.city.City
import sparespark.teamup.data.repository.CityRepository


class CityViewModel(
    private val cityRepo: CityRepository
) : BaseAdministrationViewModel<CityEvent>(
    cityRepo = cityRepo,
    clientRepo = null,
    companyRepo = null,
    productRepo = null,
    preferenceRepo = null
) {
    internal val bottomSheetViewState = MutableLiveData<Int>()

    private val cityState = MutableLiveData<City>()
    val city: LiveData<City> get() = cityState

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState


    override fun handleEvent(event: CityEvent) {
        when (event) {
            is CityEvent.OnStartGetCity -> setupCity(pos = null)
            is CityEvent.OnListItemClick -> setupCity(pos = event.pos)
            is CityEvent.OnListItemLongClick -> onListItemViewClick(event.pos)
            is CityEvent.GetCityList -> getCityList()
            is CityEvent.HideBottomSheet -> hideBottomSheet()
            is CityEvent.OnMenuRefreshClick -> clearListCacheTime()
            is CityEvent.OnMenuDeleteClick -> deleteCity(event.pos)
            is CityEvent.OnUpdateTxtClick -> updateCity(event.name)
        }
    }


    private fun setupCity(pos: Int?) = viewModelScope.launch {
        if (pos == null) cityState.value = City("", "")
        else cityState.value = cityListState.value?.get(pos)
        delay(DELAY_VIEW_EXPAND)
        expandBottomSheet()
    }

    private fun updateCity(cityName: String) = viewModelScope.launch {
        cityState.value?.let {
            showLoading()
            if (it.id.isNewStringItem())
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
        val id = cityListState.value?.get(pos)?.id
        if (id.isNullOrBlank()) return@launch

        showLoading()

        val result = when (isMultipleSelectionList(itemId = id)) {
            true -> cityRepo.deleteCity(itemsIds = getSelectionList())

            false -> cityRepo.deleteCity(itemId = id)
        }

        if (result is Result.Error) result.error.message.checkExceptionMsg(error = { showError(R.string.cannot_update_entries) }) else cityUpdated()
        hideLoading()
    }

    private fun clearListCacheTime() = viewModelScope.launch {
        showLoading()
        if (cityRepo.clearListCacheTime() is Result.Value) cityUpdated()
        else showError(R.string.cannot_update_local_entries)
        hideLoading()
    }

    private fun onListItemViewClick(pos: Int) = viewModelScope.launch {
        val itemId = cityListState.value?.get(pos)?.id
        itemId?.let { updateSelection(itemId = it, itemPos = pos) }
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