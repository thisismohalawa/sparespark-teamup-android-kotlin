package sparespark.teamup.core.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sparespark.teamup.R
import sparespark.teamup.core.NOT_PERMITTED
import sparespark.teamup.core.NO_INTERNET_CONNECTION
import sparespark.teamup.core.SERVER_DISABLE
import sparespark.teamup.core.UNAUTHORIZED
import sparespark.teamup.core.USER_DEACTIVATED
import sparespark.teamup.core.wrapper.UIResource
import sparespark.teamup.data.model.PositionSelectItem

abstract class BaseViewModel<T> : ViewModel() {
    abstract fun handleEvent(event: T)

    internal val updatePositionSelectListAttempt = MutableLiveData<PositionSelectItem>()

    private val selectionListState = mutableListOf<String>()

    private val loadingState = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = loadingState

    private val errorState = MutableLiveData<UIResource>()
    val error: LiveData<UIResource> get() = errorState

    protected fun showLoading() {
        loadingState.value = true
    }

    protected fun hideLoading() {
        loadingState.value = false
    }

    protected fun getSelectionList() = selectionListState

    protected fun isMultipleSelectionList(itemId: String): Boolean =
        when (selectionListState.size > 1) {
            true -> selectionListState.contains(itemId)
            false -> false
        }

    protected fun updateSelection(itemId: String, itemPos: Int) {
        selectionListState.contains(itemId).let { isSelect ->
            updatePositionSelectListAttempt.value = PositionSelectItem(itemPos, isSelect)
            if (isSelect) selectionListState.remove(itemId)
            else selectionListState.add(itemId)
        }
    }


    protected fun showError(msgRes: Int) {
        errorState.value = UIResource.StringResource(msgRes)
    }

    private fun showUnauthorised() {
        errorState.value = UIResource.StringResource(R.string.unauthorized)
    }

    private fun showNoConnection() {
        errorState.value = UIResource.StringResource(R.string.no_internet)
    }

    private fun showDeactivated() {
        errorState.value = UIResource.StringResource(R.string.deactivated)
    }


    private fun showNotPermitted() {
        errorState.value = UIResource.StringResource(R.string.not_permitted)
    }

    private fun showServerDisabled() {
        errorState.value = UIResource.StringResource(R.string.server_disabled)
    }

    protected fun String?.checkExceptionMsg(
        error: (() -> Unit)? = null
    ) {
        when (this) {
            NO_INTERNET_CONNECTION -> showNoConnection()
            UNAUTHORIZED -> showUnauthorised()
            USER_DEACTIVATED -> showDeactivated()
            NOT_PERMITTED -> showNotPermitted()
            SERVER_DISABLE -> showServerDisabled()
            else -> error?.invoke()
        }
    }

    protected fun String?.actionExceptionMsg(
        offline: (() -> Unit)? = null,
        serveDisable: (() -> Unit)? = null,
        unauthorised: (() -> Unit)? = null,
        deactivated: (() -> Unit)? = null,
        notPermitted: (() -> Unit)? = null,
        error: (() -> Unit)? = null
    ) {
        when (this) {
            NO_INTERNET_CONNECTION -> offline?.invoke()
            SERVER_DISABLE -> serveDisable?.invoke()
            UNAUTHORIZED -> unauthorised?.invoke()
            USER_DEACTIVATED -> deactivated?.invoke()
            NOT_PERMITTED -> notPermitted?.invoke()
            else -> error?.invoke()
        }
    }
}
