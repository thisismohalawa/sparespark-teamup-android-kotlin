package sparespark.teamup.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import sparespark.teamup.data.room.city.CityDao
import sparespark.teamup.data.room.city.RoomCity
import sparespark.teamup.data.room.client.ClientDao
import sparespark.teamup.data.room.client.RoomClient
import sparespark.teamup.data.room.company.CompanyDao
import sparespark.teamup.data.room.company.RoomCompany
import sparespark.teamup.data.room.note.NoteDao
import sparespark.teamup.data.room.note.RoomNote
import sparespark.teamup.data.room.product.ProductDao
import sparespark.teamup.data.room.product.RoomProduct
import sparespark.teamup.data.room.stock.RoomStock
import sparespark.teamup.data.room.stock.StockDao
import sparespark.teamup.data.room.transaction.RoomTransaction
import sparespark.teamup.data.room.transaction.TransactionDao
import sparespark.teamup.data.room.user.RoomUser
import sparespark.teamup.data.room.user.UserDao

private const val DATABASE = "teamup_db"

@Database(
    entities = [
        RoomUser::class,
        RoomCity::class,
        RoomClient::class,
        RoomCompany::class,
        RoomProduct::class,
        RoomNote::class,
        RoomTransaction::class,
        RoomStock::class,
    ], version = 1, exportSchema = false
)

@TypeConverters(DataConverter::class)
abstract class TeamDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun cityDao(): CityDao
    abstract fun clientDao(): ClientDao
    abstract fun companyDao(): CompanyDao
    abstract fun productDao(): ProductDao
    abstract fun noteDao(): NoteDao
    abstract fun transactionDao(): TransactionDao
    abstract fun stockDao(): StockDao

    companion object {
        @Volatile
        private var instance: TeamDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TeamDatabase::class.java, DATABASE
            ).build()
    }
}