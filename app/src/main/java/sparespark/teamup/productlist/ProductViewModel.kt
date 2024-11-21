package sparespark.teamup.productlist

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
import sparespark.teamup.data.model.CompanyEntry
import sparespark.teamup.data.model.product.Product
import sparespark.teamup.data.repository.CompanyRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.ProductRepository

class ProductViewModel(
    private val productRepo: ProductRepository,
    companyRepo: CompanyRepository,
    preferenceRepo: PreferenceRepository
) : BaseAdministrationViewModel<ProductEvent>(
    productRepo = productRepo,
    companyRepo = companyRepo,
    preferenceRepo = preferenceRepo,
    cityRepo = null,
    clientRepo = null
) {
    internal val bottomSheetViewState = MutableLiveData<Int>()

    private val productState = MutableLiveData<Product>()
    val product: LiveData<Product> get() = productState

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState

    override fun handleEvent(event: ProductEvent) {
        when (event) {
            is ProductEvent.OnStartGetProduct -> setupProduct(pos = null)
            is ProductEvent.OnListItemClick -> setupProduct(pos = event.pos)
            is ProductEvent.OnListItemLongClick -> onListItemViewClick(pos = event.pos)
            is ProductEvent.GetProductList -> getProductList()
            is ProductEvent.HideBottomSheet -> hideBottomSheet()
            is ProductEvent.OnSpinnerCompanySelect -> updateCompanyEntry(event.pos)
            is ProductEvent.OnMenuRefreshClick -> clearListCacheTime()
            is ProductEvent.OnMenuDeleteClick -> deleteProduct(event.pos)
            is ProductEvent.OnUpdateTxtClick -> updateProduct(event.product)
        }
    }

    private fun setupProduct(pos: Int?) = viewModelScope.launch {
        if (pos == null) productState.value = Product("", "", CompanyEntry())
        else productState.value = productListState.value?.get(pos)
        delay(DELAY_VIEW_EXPAND)
        expandBottomSheet()
        getCompanyList()
    }

    private fun clearListCacheTime() = viewModelScope.launch {
        showLoading()
        if (productRepo.clearListCacheTime() is Result.Value) productUpdated()
        else showError(R.string.cannot_update_local_entries)
        hideLoading()
    }

    private fun updateProduct(productName: String) = viewModelScope.launch {
        productState.value?.let {
            showLoading()
            if (it.id.isNewStringItem())
                productState.value?.id = getSystemTimeMillis()

            when (val result = productRepo.updateProduct(
                product = it.copy(name = productName)
            )) {
                is Result.Error -> result.error.message.checkExceptionMsg(error = {
                    showError(R.string.cannot_update_entries)
                })

                is Result.Value -> productUpdated()
            }
        }
        hideLoading()
    }

    private fun deleteProduct(pos: Int) = viewModelScope.launch {
        val id = productListState.value?.get(pos)?.id
        if (id.isNullOrBlank()) return@launch

        showLoading()

        val result = when (isMultipleSelectionList(itemId = id)) {
            true -> productRepo.deleteProduct(itemsIds = getSelectionList())

            false -> productRepo.deleteProduct(itemId = id)
        }

        if (result is Result.Error) result.error.message.checkExceptionMsg(error = { showError(R.string.cannot_update_entries) }) else productUpdated()
        hideLoading()
    }

    private fun onListItemViewClick(pos: Int) = viewModelScope.launch {
        val itemId = productListState.value?.get(pos)?.id
        itemId?.let { updateSelection(itemId = it, itemPos = pos) }
    }

    private fun updateCompanyEntry(pos: Int) {
        companyListState.value?.get(pos)?.let { company ->
            productState.value?.companyEntry = CompanyEntry(
                companyId = company.id,
                companyName = company.name
            )
        }
    }

    private fun productUpdated() {
        updatedState.value = Unit
    }

    private fun expandBottomSheet() {
        bottomSheetViewState.value = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideBottomSheet() {
        bottomSheetViewState.value = BottomSheetBehavior.STATE_HIDDEN
    }
}