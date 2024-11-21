package sparespark.teamup.data.room.client

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import sparespark.teamup.data.model.LocationEntry

@Entity(
    tableName = "client_table",
    indices = [Index("id")]
)
data class RoomClient(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "phone")
    var phone: String,

    @ColumnInfo(name = "location_entry")
    var locationEntry: LocationEntry
)
