package sparespark.teamup.data.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import sparespark.teamup.data.model.client.LocationEntry
import sparespark.teamup.data.model.item.AssetEntry
import sparespark.teamup.data.model.item.ClientEntry
import java.lang.reflect.Type

class DataConverter {
    @TypeConverter
    fun fromLocationEntry(item: LocationEntry?): String? {
        if (item == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<LocationEntry?>() {}.type
        return gson.toJson(item, type)
    }

    @TypeConverter
    fun toLocationEntry(string: String?): LocationEntry? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<LocationEntry?>() {}.type
        return gson.fromJson<LocationEntry>(string, type)
    }

    @TypeConverter
    fun fromClientEntry(item: ClientEntry?): String? {
        if (item == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<ClientEntry?>() {}.type
        return gson.toJson(item, type)
    }

    @TypeConverter
    fun toClientEntry(string: String?): ClientEntry? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<ClientEntry?>() {}.type
        return gson.fromJson<ClientEntry>(string, type)
    }

    @TypeConverter
    fun fromAssetEntry(item: AssetEntry?): String? {
        if (item == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<AssetEntry?>() {}.type
        return gson.toJson(item, type)
    }

    @TypeConverter
    fun toAssetEntry(string: String?): AssetEntry? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<AssetEntry?>() {}.type
        return gson.fromJson<AssetEntry>(string, type)
    }
}
