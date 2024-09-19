package sparespark.teamup.data.room.expense

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "expense_table", indices = [Index("id")])
data class RoomExpense(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "creation_date")
    val creationDate: String,

    @ColumnInfo(name = "created_by")
    val createdBy: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "cost")
    val cost: Double,

    @ColumnInfo(name = "note")
    val note: String,

    @ColumnInfo(name = "income")
    val income: Boolean,

    @ColumnInfo(name = "team")
    val team: Boolean,
)
