package sparespark.teamup.data.room.expense

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(roomExpense: RoomExpense): Long

    @Query("SELECT * FROM expense_table")
    suspend fun getList(): List<RoomExpense>

    @Query("DELETE FROM expense_table")
    suspend fun clearList()

    @Query("DELETE FROM expense_table where id = :itemId")
    suspend fun deleteItem(itemId:String)

}
