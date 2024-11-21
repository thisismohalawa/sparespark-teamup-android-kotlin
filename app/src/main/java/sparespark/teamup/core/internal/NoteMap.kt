package sparespark.teamup.core.internal

import sparespark.teamup.data.model.note.Note
import sparespark.teamup.data.model.note.RemoteNote
import sparespark.teamup.data.room.note.RoomNote

internal val Note.toRoomNote: RoomNote
    get() = RoomNote(
        this.id,
        this.title,
        this.onlyAdmins,
        this.creationDate,
        this.createdBy
    )
internal val RoomNote.toNote: Note
    get() = Note(
        this.id,
        this.title,
        this.onlyAdmins,
        this.creationDate,
        this.createdBy
    )
internal val RemoteNote.toNote: Note
    get() = Note(
        this.id ?: "",
        this.title ?: "",
        this.onlyAdmins ?: false,
        this.creationDate ?: "",
        this.createdBy ?: ""
    )
internal val Note.toRemoteNote: RemoteNote
    get() = RemoteNote(
        this.id,
        this.title,
        this.onlyAdmins,
        this.creationDate,
        this.createdBy
    )

internal fun List<RoomNote>.toNoteList(): List<Note> =
    this.flatMap {
        listOf(it.toNote)
    }