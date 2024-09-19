package sparespark.teamup.item.itemlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.note.Note
import sparespark.teamup.data.preference.selector.LocalSelectorRepository
import sparespark.teamup.data.repository.ItemRepository
import sparespark.teamup.data.repository.NoteRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.StaticsRepository
import sparespark.teamup.item.BaseItemListViewModel

abstract class BaseItemListNoteViewModel<VE, AVE>(
    itemRepo: ItemRepository,
    staticsRepo: StaticsRepository,
    selectorRepo: LocalSelectorRepository,
    preferenceRepo: PreferenceRepository,
    private val noteRepo: NoteRepository
) : BaseItemListViewModel<VE, AVE>(itemRepo, staticsRepo, selectorRepo, preferenceRepo) {

    internal val useNotesState = MutableLiveData<Boolean>()

    private val noteListState = MutableLiveData<List<Note>>()
    val noteList: LiveData<List<Note>> get() = noteListState

    private val updatedNoteState = MutableLiveData<Unit>()
    val updatedNote: LiveData<Unit> get() = updatedNoteState

    private val editNoteState = MutableLiveData<Event<String>>()
    val editNote: LiveData<Event<String>> get() = editNoteState

    protected fun updateNoteUse() {
        useNotesState.value = isNoteEnable()
        if (useNotesState.value == true) getNotes()
    }

    protected fun editNote(pos: Int) {
        editNoteState.value = Event(noteListState.value?.get(pos)?.id.toString())
    }

    protected fun clearNoteListCacheTime() = viewModelScope.launch {
        showLoading()
        if (noteRepo.clearListCacheTime() is Result.Value) updatedNoteState.value = Unit
        hideLoading()
    }

    protected fun deleteNote(pos: Int) = viewModelScope.launch {
        showLoading()
        val noteId = noteListState.value?.get(pos)?.id
        when (val result = noteId?.let { noteRepo.deleteNote(noteId = it) }) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.cannot_update_entries)
            })

            is Result.Value -> updatedNoteState.value = Unit
            null -> showError(R.string.cannot_read_local_data)
        }
        hideLoading()
    }

    private fun getLocalNotes() = viewModelScope.launch {
        val result = noteRepo.getNotes(localOnly = Unit)
        if (result is Result.Value) noteListState.value = result.value.asReversed()
        else showError(R.string.cannot_read_local_data)
    }

    private fun getNotes() = viewModelScope.launch {
        showLoading()
        when (val result = noteRepo.getNotes()) {
            is Result.Error -> result.error.message.actionExceptionMsg(offline = {
                getLocalNotes()
            }, unauthorised = { Unit }, deactivated = { Unit }, error = {
                showError(R.string.error_retrieve_notelist)
            })

            is Result.Value -> noteListState.value = result.value.asReversed()
        }
        hideLoading()
    }
}