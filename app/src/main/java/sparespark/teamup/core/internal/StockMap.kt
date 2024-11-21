package sparespark.teamup.core.internal

import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ClientEntry
import sparespark.teamup.data.model.ProductEntry
import sparespark.teamup.data.model.stock.RemoteStock
import sparespark.teamup.data.model.stock.Stock
import sparespark.teamup.data.room.stock.RoomStock

internal val RoomStock.toStock: Stock
    get() = Stock(
        id = this.id,
        creationDate = this.creationDate,
        productEntry = this.productEntry,
        datasourceId = this.datasourceId,
        clientEntry = this.clientEntry,
        assetEntry = this.assetEntry,
        note = this.note,
        sell = this.sell,
        temp = this.temp,
        updateBy = this.updateBy,
        updateDate = this.updateDate
    )
internal val RemoteStock.toStock: Stock
    get() = Stock(
        id = this.id ?: "",
        creationDate = this.creationDate ?: "",
        datasourceId = this.datasourceId ?: 0,
        productEntry = this.productEntry ?: ProductEntry(),
        clientEntry = this.clientEntry ?: ClientEntry(),
        assetEntry = this.assetEntry ?: AssetEntry(),
        note = this.note ?: "",
        sell = this.sell ?: false,
        temp = this.temp ?: false,
        updateBy = this.updateBy ?: "",
        updateDate = this.updateDate ?: ""
    )
internal val Stock.toRemoteStock: RemoteStock
    get() = RemoteStock(
        id = this.id,
        creationDate = this.creationDate,
        datasourceId = this.datasourceId,
        productEntry = this.productEntry,
        clientEntry = this.clientEntry,
        assetEntry = this.assetEntry,
        note = this.note,
        sell = this.sell,
        temp = this.temp,
        updateBy = this.updateBy,
        updateDate = this.updateDate
    )
internal val Stock.toRoomStock: RoomStock
    get() = RoomStock(
        id = this.id,
        creationDate = this.creationDate,
        datasourceId = this.datasourceId,
        productEntry = this.productEntry,
        clientEntry = this.clientEntry,
        assetEntry = this.assetEntry,
        note = this.note,
        sell = this.sell,
        temp = this.temp,
        updateBy = this.updateBy,
        updateDate = this.updateDate
    )

internal fun List<RoomStock>.toStockList(): List<Stock> = this.flatMap {
    listOf(it.toStock)
}