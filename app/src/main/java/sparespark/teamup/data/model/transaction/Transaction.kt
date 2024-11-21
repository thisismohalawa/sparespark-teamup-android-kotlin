package sparespark.teamup.data.model.transaction

import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ClientEntry

data class Transaction(
    var id: String,
    var creationDate: String,
    var clientEntry: ClientEntry,
    var assetEntry: AssetEntry,
    val note: String,
    var active: Boolean,
    var temp: Boolean,
    var sell: Boolean,
    val updateBy: String,
    var updateDate: String
)

