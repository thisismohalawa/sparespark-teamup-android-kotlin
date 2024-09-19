package sparespark.teamup.data.room.item

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(roomItemX: RoomItemX): Long

    @Query("UPDATE itemx_table SET active=:isActive WHERE id = :itemId")
    suspend fun updateItem(itemId: String, isActive: Boolean)

    @Query("SELECT * FROM itemx_table ")
    suspend fun getList(): List<RoomItemX>

    @Query("SELECT * FROM itemx_table WHERE id = :itemId")
    suspend fun getItemById(itemId: String): RoomItemX

    @Query("DELETE FROM itemx_table")
    suspend fun clearList()

    @Query("DELETE FROM itemx_table where id = :itemId")
    suspend fun deleteItem(itemId: String)
 }
