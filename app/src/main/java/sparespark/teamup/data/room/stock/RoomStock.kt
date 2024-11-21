package sparespark.teamup.data.room.stock

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ClientEntry
import sparespark.teamup.data.model.ProductEntry

@Entity(tableName = "stock_table", indices = [Index("id")])
data class RoomStock(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "creation_date")
    val creationDate: String,

    @ColumnInfo(name = "datasource_id")
    val datasourceId: Int,

    @ColumnInfo(name = "product_entry")
    val productEntry: ProductEntry,

    @ColumnInfo(name = "client_entry")
    val clientEntry: ClientEntry,

    @ColumnInfo(name = "asset_entry")
    val assetEntry: AssetEntry,

    @ColumnInfo(name = "note")
    val note: String,

    @ColumnInfo(name = "sell")
    val sell: Boolean,

    @ColumnInfo(name = "temp_item")
    val temp: Boolean,

    @ColumnInfo(name = "update_by")
    val updateBy: String,

    @ColumnInfo(name = "update_date")
    var updateDate: String,
)
