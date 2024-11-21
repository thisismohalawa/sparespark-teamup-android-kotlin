package sparespark.teamup.data.room.stock

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(roomStock: RoomStock): Long

    @Query("UPDATE stock_table SET temp_item=:isTemp WHERE id = :itemId")
    suspend fun updateTemp(itemId: String, isTemp: Boolean)

    @Query("SELECT * FROM stock_table ")
    suspend fun getList(): List<RoomStock>

    @Query("SELECT * FROM stock_table WHERE id = :itemId")
    suspend fun getItemById(itemId: String): RoomStock

    @Query("DELETE FROM stock_table")
    suspend fun clearList()

    @Query("DELETE FROM stock_table where id = :itemId")
    suspend fun deleteItem(itemId: String)
}
