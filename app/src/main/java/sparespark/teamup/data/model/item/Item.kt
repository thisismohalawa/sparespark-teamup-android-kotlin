package sparespark.teamup.data.model.item

data class Item(
    var id: String,
    var creationDate: String,
    var clientEntry: ClientEntry,
    var assetEntry: AssetEntry,
    val note: String,
    var active: Boolean,
    var onlyAdmins: Boolean,
    var sell: Boolean,
    val updateBy: String,
    var updateDate: String,
)

