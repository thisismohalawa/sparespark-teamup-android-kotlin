package sparespark.teamup.core.map

import sparespark.teamup.data.model.item.AssetEntry
import sparespark.teamup.data.model.item.ClientEntry
import sparespark.teamup.data.model.item.Item
import sparespark.teamup.data.model.item.RemoteItemX
import sparespark.teamup.data.room.item.RoomItemX

internal const val DEF_ITEM_SELL = true
internal const val DEF_ITEM_ACTIVE = true
internal const val DEF_ITEM_ADMIN_CRUD = false

internal val RoomItemX.toItem: Item
    get() = Item(
        id = this.id,
        creationDate = this.creationDate,
        clientEntry = this.clientEntry,
        assetEntry = this.assetEntry,
        note = this.note,
        active = this.active,
        onlyAdmins = this.onlyAdmins,
        sell = this.sell,
        updateBy = this.updateBy,
        updateDate = this.updateDate
    )
internal val RemoteItemX.toItem: Item
    get() = Item(
        id = this.id ?: "",
        creationDate = this.creationDate ?: "",
        clientEntry = this.clientEntry ?: ClientEntry(),
        assetEntry = this.assetEntry ?: AssetEntry(),
        note = this.note ?: "",
        active = this.active ?: DEF_ITEM_ACTIVE,
        onlyAdmins = this.onlyAdmins ?: DEF_ITEM_ADMIN_CRUD,
        sell = this.sell ?: DEF_ITEM_SELL,
        updateBy = this.updateBy ?: "",
        updateDate = this.updateDate ?: ""
    )
internal val Item.toRemoteItem: RemoteItemX
    get() = RemoteItemX(
        id = this.id,
        creationDate = this.creationDate,
        clientEntry = this.clientEntry,
        assetEntry = this.assetEntry,
        note = this.note,
        active = this.active,
        onlyAdmins = this.onlyAdmins,
        sell = this.sell,
        updateBy = this.updateBy,
        updateDate = this.updateDate
    )
internal val Item.toRoomItem: RoomItemX
    get() = RoomItemX(
        id = this.id,
        creationDate = this.creationDate,
        clientEntry = this.clientEntry,
        assetEntry = this.assetEntry,
        note = this.note,
        active = this.active,
        onlyAdmins = this.onlyAdmins,
        sell = this.sell,
        updateBy = this.updateBy,
        updateDate = this.updateDate
    )

internal fun List<RoomItemX>.toListItemX(): List<Item> = this.flatMap {
    listOf(it.toItem)
}

