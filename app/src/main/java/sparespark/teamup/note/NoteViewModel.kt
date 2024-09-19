package sparespark.teamup.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.MAX_NOTE_DIG
import sparespark.teamup.core.getCalendarDateTime
import sparespark.teamup.core.getSystemTimeMillis
import sparespark.teamup.core.isNewIdItem
import sparespark.teamup.core.isValidLength
import sparespark.teamup.core.map.DEF_ITEM_ADMIN_CRUD
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.note.Note
import sparespark.teamup.data.repository.NoteRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.preferences.base.BasePreferenceViewModel

class NoteViewModel(
    private val noteRepo: NoteRepository,
    preferenceRepo: PreferenceRepository,
    savedStateHandle: SavedStateHandle
) : BasePreferenceViewModel<NoteEvent>(preferenceRepo) {

    internal val adminSwitchState = MutableLiveData<Boolean>()
    internal val noteTxtValidateState = MutableLiveData<Unit>()
    internal val shareNoteAttempt = MutableLiveData<Event<String>>()

    private val navNoteId: String? = savedStateHandle["noteId"]

    private val noteState = MutableLiveData<Note>()
    val note: LiveData<Note> get() = noteState

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState

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
            noteState.value = Note("", "", DEF_ITEM_ADMIN_CRUD, "", "")
            adminSwitchState.value = DEF_ITEM_ADMIN_CRUD
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
                if (it.id.isNewIdItem()) {
                    it.id = getSystemTimeMillis()
                    it.creationDate = getCalendarDateTime()
                }
                when (val result = noteRepo.updateNote(
                    it.copy(
                        title = title,
                        onlyAdmins = adminSwitchState.value ?: DEF_ITEM_ADMIN_CRUD
                    )
                )) {
                    is Result.Error -> result.error.message.checkExceptionMsg(error = {
                        showError(R.string.cannot_update_entries)
                    })

                    is Result.Value -> {
                        if (isSuggestShareEnable()) shareNoteAttempt.value =
                            Event("\"$title\"\n${noteState.value?.creationDate}")
                        updatedState.value = Unit
                    }
                }
            }
        hideLoading()
    }

    private fun isValidInput(title: String): Boolean = if (!title.isValidLength(MAX_NOTE_DIG)) {
        noteTxtValidateState.value = Unit
        false
    } else true
}

