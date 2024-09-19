package sparespark.teamup.core.map

import sparespark.teamup.data.model.client.Client
import sparespark.teamup.data.model.client.LocationEntry
import sparespark.teamup.data.model.client.RemoteClient
import sparespark.teamup.data.room.client.RoomClient

internal val RoomClient.toClient: Client
    get() = Client(
        id = this.id,
        name = this.name,
        phone = this.phone,
        locationEntry = this.locationEntry
    )
internal val RemoteClient.toClient: Client
    get() = Client(
        id = this.id ?: "",
        name = this.name ?: "",
        phone = this.phone ?: "",
        locationEntry = this.locationEntry ?: LocationEntry()
    )
internal val Client.toRemoteClient: RemoteClient
    get() = RemoteClient(
        id = this.id,
        name = this.name,
        phone = this.phone,
        locationEntry = this.locationEntry
    )
internal val Client.toRoomClient: RoomClient
    get() = RoomClient(
        id = this.id,
        name = this.name,
        phone = this.phone,
        locationEntry = this.locationEntry
    )

internal fun List<RoomClient>.toClientList(): List<Client> = this.flatMap {
    listOf(it.toClient)
}
