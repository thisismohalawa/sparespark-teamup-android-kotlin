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
import sparespark.teamup.data.room.expense.ExpenseDao
import sparespark.teamup.data.room.expense.RoomExpense
import sparespark.teamup.data.room.item.ItemDao
import sparespark.teamup.data.room.item.RoomItemX
import sparespark.teamup.data.room.note.NoteDao
import sparespark.teamup.data.room.note.RoomNote
import sparespark.teamup.data.room.user.RoomUser
import sparespark.teamup.data.room.user.UserDao

private const val DATABASE = "team_db"

@Database(
    entities = [
        RoomUser::class,
        RoomItemX::class,
        RoomClient::class,
        RoomNote::class,
        RoomCity::class,
        RoomExpense::class
    ], version = 1, exportSchema = false
)
@TypeConverters(DataConverter::class)
abstract class TeamDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun itemDao(): ItemDao
    abstract fun clientDao(): ClientDao
    abstract fun noteDao(): NoteDao
    abstract fun cityDao(): CityDao
    abstract fun expenseDao(): ExpenseDao

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