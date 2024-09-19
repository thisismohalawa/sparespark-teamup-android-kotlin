package sparespark.teamup.note.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import sparespark.teamup.data.repository.NoteRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.note.NoteViewModel

class NoteViewModelFactory(
    private val noteRepo: NoteRepository,
    private val preferenceRepo: PreferenceRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        if (modelClass.isAssignableFrom(NoteViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            NoteViewModel(noteRepo, preferenceRepo, extras.createSavedStateHandle()) as T
        else throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
}
