package sparespark.teamup.data.model.stock

import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ClientEntry
import sparespark.teamup.data.model.ProductEntry

data class Stock(
    var id: String,
    var creationDate: String,
    var datasourceId: Int,
    var productEntry: ProductEntry,
    var clientEntry: ClientEntry,
    var assetEntry: AssetEntry,
    val note: String,
    val sell: Boolean,
    var temp: Boolean,
    var updateBy: String,
    var updateDate: String
)

