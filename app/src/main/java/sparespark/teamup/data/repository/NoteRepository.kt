package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.note.Note

interface NoteRepository {
    suspend fun getNotes(localOnly: Unit? = null): Result<Exception, List<Note>>
    suspend fun getNoteById(noteId: String): Result<Exception, Note>
    suspend fun updateNote(note: Note): Result<Exception, Unit>
    suspend fun deleteNote(
        noteId: String,
    ): Result<Exception, Unit>

    suspend fun clearListCacheTime(): Result<Exception, Unit>
}