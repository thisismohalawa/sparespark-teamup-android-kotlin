package sparespark.teamup.data.model.stock

import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ClientEntry
import sparespark.teamup.data.model.ProductEntry

data class RemoteStock(
    var id: String? = "",
    var creationDate: String? = "",
    var datasourceId: Int? = 0,
    var productEntry: ProductEntry? = ProductEntry(),
    var clientEntry: ClientEntry? = ClientEntry(),
    var assetEntry: AssetEntry? = AssetEntry(),
    val note: String? = "",
    val sell: Boolean? = false,
    var temp: Boolean? = false,
    val updateBy: String? = "",
    var updateDate: String? = ""
)

