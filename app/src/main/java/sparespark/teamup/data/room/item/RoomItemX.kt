package sparespark.teamup.data.room.item

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import sparespark.teamup.data.model.item.AssetEntry
import sparespark.teamup.data.model.item.ClientEntry

@Entity(tableName = "itemx_table", indices = [Index("id")])
data class RoomItemX(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "creation_date")
    val creationDate: String,

    @ColumnInfo(name = "note")
    val note: String,

    @ColumnInfo(name = "client_entry")
    val clientEntry: ClientEntry,

    @ColumnInfo(name = "asset_entry")
    val assetEntry: AssetEntry,

    @ColumnInfo(name = "active")
    val active: Boolean,

    @ColumnInfo(name = "only_admins")
    val onlyAdmins: Boolean,

    @ColumnInfo(name = "sell")
    var sell: Boolean,

    @ColumnInfo(name = "update_by")
    val updateBy: String,

    @ColumnInfo(name = "update_date")
    var updateDate: String,
)
