package sparespark.teamup.data.model.item

import sparespark.teamup.core.map.DEF_ITEM_ACTIVE
import sparespark.teamup.core.map.DEF_ITEM_ADMIN_CRUD
import sparespark.teamup.core.map.DEF_ITEM_SELL


data class RemoteItemX(
    val id: String? = "",
    var creationDate: String? = "",
    val note: String? = "",
    val clientEntry: ClientEntry? = ClientEntry(),
    var assetEntry: AssetEntry? = AssetEntry(),
    var active: Boolean? = DEF_ITEM_ACTIVE,
    var onlyAdmins: Boolean? = DEF_ITEM_ADMIN_CRUD,
    var sell: Boolean? = DEF_ITEM_SELL,
    val updateBy: String? = "",
    var updateDate: String? = "",
)
