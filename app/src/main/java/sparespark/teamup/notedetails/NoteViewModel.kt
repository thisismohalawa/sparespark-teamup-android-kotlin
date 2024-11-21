package sparespark.teamup.notedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.MAX_INPUT_NOTE_DIG
import sparespark.teamup.core.MIN_INPUT_TITLE_DIG
import sparespark.teamup.core.base.BaseViewModel
import sparespark.teamup.core.getCalendarDateTime
import sparespark.teamup.core.getSystemTimeMillis
import sparespark.teamup.core.isNewStringItem
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.note.Note
import sparespark.teamup.data.repository.NoteRepository

class NoteViewModel(
    private val noteRepo: NoteRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<NoteEvent>() {

    internal val adminSwitchState = MutableLiveData<Boolean>()
    internal val noteTxtValidateState = MutableLiveData<Unit>()

    private val navNoteId: String? = savedStateHandle["noteId"]

    private val noteState = MutableLiveData<Note>()
    val note: LiveData<Note> get() = noteState

    private val updatedState = MutableLiveData<Event<Unit>>()
    val updated: LiveData<Event<Unit>> get() = updatedState

    override fun handleEvent(event: NoteEvent) {
        when (event) {
            is NoteEvent.OnStartGetNote -> getNote()
            is NoteEvent.OnAdminSwitchCheck -> adminSwitchState.value = event.admin
            is NoteEvent.OnUpdateTxtClick -> updateNote(event.title)
        }
    }

    private fun getNote() = viewModelScope.launch {
        showLoading()
        if (navNoteId.isNullOrEmpty()) {
            noteState.value = Note("", "", false, "", "")
            adminSwitchState.value = false
        } else
            when (val result = noteRepo.getNoteById(navNoteId)) {
                is Result.Error -> result.error.message.checkExceptionMsg(error = {
                    showError(R.string.error_retrieve_notelist)
                })

                is Result.Value -> {
                    noteState.value = result.value
                    adminSwitchState.value = result.value.onlyAdmins
                }
            }
        hideLoading()
    }

    private fun updateNote(title: String) = viewModelScope.launch {
        if (isValidInput(title))
            noteState.value?.let {
                showLoading()
                if (it.id.isNewStringItem()) {
                    it.id = getSystemTimeMillis()
                    it.creationDate = getCalendarDateTime()
                }
                when (val result = noteRepo.updateNote(
                    it.copy(
                        title = title,
                        onlyAdmins = adminSwitchState.value ?: false
                    )
                )) {
                    is Result.Error -> result.error.message.checkExceptionMsg(error = {
                        showError(R.string.cannot_update_entries)
                    })

                    is Result.Value -> updatedState.value = Event(Unit)

                }
            }
        hideLoading()
    }

    private fun isValidInput(title: String): Boolean =
        if (title.length !in MIN_INPUT_TITLE_DIG..MAX_INPUT_NOTE_DIG) {
            noteTxtValidateState.value = Unit
            false
        } else true
}

