package sparespark.teamup.item.itemlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.DELAY_NAV_RESTART
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.item.Item
import sparespark.teamup.data.model.statics.CStatics
import sparespark.teamup.data.preference.selector.LocalSelectorRepository
import sparespark.teamup.data.repository.ItemRepository
import sparespark.teamup.data.repository.NoteRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.StaticsRepository
import sparespark.teamup.note.NoteListEvent

open class ItemListViewModel(
    itemRepo: ItemRepository,
    staticsRepo: StaticsRepository,
    selectorRepo: LocalSelectorRepository,
    preferenceRepo: PreferenceRepository,
    noteRepo: NoteRepository,
) : BaseItemListNoteViewModel<ItemListEvent, NoteListEvent>(
    itemRepo, staticsRepo, selectorRepo, preferenceRepo, noteRepo
) {
    companion object {
        private var backExitPressed = false
    }

    internal val exitState = MutableLiveData<Unit>()
    internal val useCalendarStaticsState = MutableLiveData<Boolean>()

    private val calenderStaticsState = MutableLiveData(CStatics())
    val calenderStatics: LiveData<CStatics> get() = calenderStaticsState

    override fun handleAttachViewEvent(event: NoteListEvent) {
        when (event) {
            is NoteListEvent.GetNoteList -> updateNoteUse()
            is NoteListEvent.OnMenuNoteListRefresh -> clearNoteListCacheTime()
            is NoteListEvent.OnNoteItemClick -> editNote(event.pos)
            is NoteListEvent.OnMenuNoteListDelete -> deleteNote(event.pos)
        }
    }

    override fun handleViewEvent(event: ItemListEvent) {
        when (event) {
            is ItemListEvent.OnViewStart -> {
                updateCalenderUse()
                updateListStaticUse()
                getItemList(resultAction = {

                })
                clearSelectionSet()
                updateItemListHintTitle()
            }

            is ItemListEvent.OnViewBackPressed -> viewBackPressed()
        }
    }

    private fun updateCalenderUse() {
        useCalendarStaticsState.value = isCalenderStaticsEnable()
        if (useCalendarStaticsState.value == true) getCalenderStatics()
    }

    private fun updateListStaticUse() {
        useListStaticsState.value = isListStaticsEnable()
        if (useListStaticsState.value == true) getListStatics()
    }

    private fun getCalenderStatics() = viewModelScope.launch {
        fun errorStatics() {
            calenderStaticsState.value = CStatics(-1, -1, -1)
        }
        when (val result = staticsRepo.getCalenderStatics()) {
            is Result.Error -> result.error.message.actionExceptionMsg(
                unauthorised = {
                    itemListState.value?.calculateCalenderStatics()
                },
                deactivated = {
                    errorStatics()
                },
                notPermitted = {
                    errorStatics()
                },
                offline = {
                    CStatics(0, 0, 0)
                },
                serveDisable = {
                    itemListState.value?.calculateCalenderStatics()
                },
                error = {
                    errorStatics()
                    showError(R.string.cannot_read_statistics)
                }
            )

            is Result.Value -> calenderStaticsState.value = result.value
        }
    }

    private fun List<Item>.calculateCalenderStatics() = viewModelScope.launch {
        val result = staticsRepo.calculateCalenderStatics(this@calculateCalenderStatics)
        if (result is Result.Value) calenderStaticsState.value = result.value
        else showError(R.string.cannot_read_statistics)
    }

    private fun updateItemListHintTitle() = viewModelScope.launch {
        val result = itemRepo.getItemListHintTitle()
        if (result is Result.Value) updateItemListHintAttempt.value = result.value
    }

    private fun viewBackPressed() = viewModelScope.launch {
        if (backExitPressed) {
            exitState.value = Unit
        }
        clearSelectionSet(unselectItems = Unit)
        backExitPressed = true
        delay(DELAY_NAV_RESTART)
        backExitPressed = false
    }
}