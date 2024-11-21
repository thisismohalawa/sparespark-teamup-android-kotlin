package sparespark.teamup.data.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ClientEntry
import sparespark.teamup.data.model.CompanyEntry
import sparespark.teamup.data.model.LocationEntry
import sparespark.teamup.data.model.ProductEntry
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

    @TypeConverter
    fun fromCompanyEntry(item: CompanyEntry?): String? {
        if (item == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<CompanyEntry?>() {}.type
        return gson.toJson(item, type)
    }

    @TypeConverter
    fun toCompanyEntry(string: String?): CompanyEntry? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<CompanyEntry?>() {}.type
        return gson.fromJson<CompanyEntry>(string, type)
    }

    @TypeConverter
    fun fromProductEntry(item: ProductEntry?): String? {
        if (item == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<ProductEntry?>() {}.type
        return gson.toJson(item, type)
    }

    @TypeConverter
    fun toProductEntry(string: String?): ProductEntry? {
        if (string == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<ProductEntry?>() {}.type
        return gson.fromJson<ProductEntry>(string, type)
    }
}
