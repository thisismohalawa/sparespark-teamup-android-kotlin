package sparespark.teamup.data.room.product

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import sparespark.teamup.data.model.CompanyEntry


@Entity(
    tableName = "product_table",
    indices = [Index("id")]
)
data class RoomProduct(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "company_entry")
    var companyEntry: CompanyEntry
)
