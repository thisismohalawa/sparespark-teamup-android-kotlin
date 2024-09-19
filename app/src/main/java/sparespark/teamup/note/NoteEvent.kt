package sparespark.teamup.note


sealed class NoteEvent {
    data object OnStartGetNote : NoteEvent()
    data class OnAdminSwitchCheck(var admin: Boolean) : NoteEvent()
    data class OnUpdateTxtClick(val title: String) : NoteEvent()
}
