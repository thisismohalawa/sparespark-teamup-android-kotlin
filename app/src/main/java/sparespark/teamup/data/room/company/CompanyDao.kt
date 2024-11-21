package sparespark.teamup.data.room.company

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface CompanyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(company: RoomCompany): Long

    @Query("SELECT * FROM company_table")
    suspend fun getList(): List<RoomCompany>

    @Query("DELETE FROM company_table where id = :companyId")
    suspend fun deleteCompany(companyId: String)

    @Query("DELETE FROM company_table")
    suspend fun clearList()
}
