package sparespark.teamup.data.room.note

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "notes",
    indices = [Index("id")]
)
data class RoomNote(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "only_admins")
    val onlyAdmins: Boolean,

    @ColumnInfo(name = "creation_date")
    val creationDate: String,

    @ColumnInfo(name = "created_by")
    val createdBy: String
)
