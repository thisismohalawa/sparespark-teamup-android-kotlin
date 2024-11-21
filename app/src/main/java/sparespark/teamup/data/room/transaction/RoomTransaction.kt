package sparespark.teamup.data.room.transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ClientEntry

@Entity(tableName = "transaction_table", indices = [Index("id")])
data class RoomTransaction(
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

    @ColumnInfo(name = "temp_item")
    val temp: Boolean,

    @ColumnInfo(name = "sell")
    var sell: Boolean,

    @ColumnInfo(name = "update_by")
    val updateBy: String,

    @ColumnInfo(name = "update_date")
    var updateDate: String,
)
