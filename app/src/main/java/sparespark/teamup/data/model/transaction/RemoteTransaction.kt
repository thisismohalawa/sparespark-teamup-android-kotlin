package sparespark.teamup.data.model.transaction

import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ClientEntry


data class RemoteTransaction(
    val id: String? = "",
    var creationDate: String? = "",
    val note: String? = "",
    val clientEntry: ClientEntry? = ClientEntry(),
    var assetEntry: AssetEntry? = AssetEntry(),
    var active: Boolean? = true,
    var temp: Boolean? = false,
    var sell: Boolean? = true,
    val updateBy: String? = "",
    var updateDate: String? = "",
)
