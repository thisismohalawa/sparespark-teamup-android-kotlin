package sparespark.teamup.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import sparespark.teamup.core.START_ITEM_EXPORT
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.data.model.ISelect
import sparespark.teamup.data.model.item.Item
import sparespark.teamup.data.model.statics.LStatics
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.preferences.base.BasePreferenceViewModel

abstract class BaseItemListEventViewModel<DE, VE, AVE>(
    preferenceRepo: PreferenceRepository
) :
    BasePreferenceViewModel<DE>(preferenceRepo) {

    abstract fun handleViewEvent(event: VE)

    abstract fun handleAttachViewEvent(event: AVE)

    internal val updateItemListHintAttempt = MutableLiveData<List<Int>>()
    internal val updateItemSelectAttempt = MutableLiveData<ISelect>()
    internal val updateItemListSelectAttempt = MutableLiveData<List<Int>>()
    internal val exportItemListAttempt = MutableLiveData<Event<String>>()
    internal val shareItemListAttempt = MutableLiveData<Event<String?>>()
    internal val copyItemListAttempt = MutableLiveData<Event<String?>>()
    internal val useListStaticsState = MutableLiveData<Boolean>()

    protected val itemListState = MutableLiveData<List<Item>>()
    val itemList: LiveData<List<Item>> get() = itemListState

    protected val listStaticsState = MutableLiveData(LStatics())
    val listStatics: LiveData<LStatics> get() = listStaticsState

    private val editItemState = MutableLiveData<Event<String>>()
    val editItem: LiveData<Event<String>> get() = editItemState

    private val updateState = MutableLiveData<Unit>()
    val updatedItem: LiveData<Unit> get() = updateState

    protected fun editItem(pos: Int) {
        editItemState.value = itemListState.value?.get(pos)?.let { Event(it.id) }
    }

    protected fun itemListUpdated() {
        updateState.value = Unit
    }

    protected fun exportItemList() {
        exportItemListAttempt.value = Event(START_ITEM_EXPORT)
    }
}