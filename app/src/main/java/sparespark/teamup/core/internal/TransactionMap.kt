package sparespark.teamup.core.internal

import sparespark.teamup.data.model.AssetEntry
import sparespark.teamup.data.model.ClientEntry
import sparespark.teamup.data.model.transaction.RemoteTransaction
import sparespark.teamup.data.model.transaction.Transaction
import sparespark.teamup.data.room.transaction.RoomTransaction


internal val RoomTransaction.toTransaction: Transaction
    get() = Transaction(
        id = this.id,
        creationDate = this.creationDate,
        clientEntry = this.clientEntry,
        assetEntry = this.assetEntry,
        note = this.note,
        active = this.active,
        temp = this.temp,
        sell = this.sell,
        updateBy = this.updateBy,
        updateDate = this.updateDate
    )
internal val RemoteTransaction.toTransaction: Transaction
    get() = Transaction(
        id = this.id ?: "",
        creationDate = this.creationDate ?: "",
        clientEntry = this.clientEntry ?: ClientEntry(),
        assetEntry = this.assetEntry ?: AssetEntry(),
        note = this.note ?: "",
        active = this.active ?: true,
        temp = this.temp ?: false,
        sell = this.sell ?: true,
        updateBy = this.updateBy ?: "",
        updateDate = this.updateDate ?: ""
    )
internal val Transaction.toRemoteTransaction: RemoteTransaction
    get() = RemoteTransaction(
        id = this.id,
        creationDate = this.creationDate,
        clientEntry = this.clientEntry,
        assetEntry = this.assetEntry,
        note = this.note,
        active = this.active,
        temp = this.temp,
        sell = this.sell,
        updateBy = this.updateBy,
        updateDate = this.updateDate
    )
internal val Transaction.toRoomTransaction: RoomTransaction
    get() = RoomTransaction(
        id = this.id,
        creationDate = this.creationDate,
        clientEntry = this.clientEntry,
        assetEntry = this.assetEntry,
        note = this.note,
        active = this.active,
        temp = this.temp,
        sell = this.sell,
        updateBy = this.updateBy,
        updateDate = this.updateDate
    )

internal fun List<RoomTransaction>.toTransactionList(): List<Transaction> = this.flatMap {
    listOf(it.toTransaction)
}


