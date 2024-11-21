package sparespark.teamup.notedetails


sealed class NoteListEvent {
    data object GetNoteList : NoteListEvent()
    data class OnNoteItemClick(val pos: Int) : NoteListEvent()
    data class OnMenuNoteListDelete(var pos: Int) : NoteListEvent()
    data object OnMenuNoteListRefresh : NoteListEvent()
}
