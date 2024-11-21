package sparespark.teamup.data.room.transaction

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(roomTransaction: RoomTransaction): Long

    @Query("UPDATE transaction_table SET active=:isActive WHERE id = :itemId")
    suspend fun activateItem(itemId: String, isActive: Boolean)

    @Query("UPDATE transaction_table SET update_by=:updateBy, update_date=:updateDate WHERE id = :itemId")
    suspend fun updateHistory(itemId: String, updateBy: String, updateDate: String)

    @Query("UPDATE transaction_table SET temp_item=:isTemp WHERE id = :itemId")
    suspend fun updateTemp(itemId: String, isTemp: Boolean)

    @Query("SELECT * FROM transaction_table ")
    suspend fun getList(): List<RoomTransaction>

    @Query("SELECT * FROM transaction_table WHERE id = :itemId")
    suspend fun getItemById(itemId: String): RoomTransaction

    @Query("DELETE FROM transaction_table")
    suspend fun clearList()

    @Query("DELETE FROM transaction_table where id = :itemId")
    suspend fun deleteItem(itemId: String)
}
