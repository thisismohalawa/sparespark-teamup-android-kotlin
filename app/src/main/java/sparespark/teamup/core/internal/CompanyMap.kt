package sparespark.teamup.core.internal

import sparespark.teamup.data.model.company.Company
import sparespark.teamup.data.model.company.RemoteCompany
import sparespark.teamup.data.room.company.RoomCompany

internal val RoomCompany.toCompany: Company
    get() = Company(
        id = this.id,
        name = this.name,
    )
internal val RemoteCompany.toCompany: Company
    get() = Company(
        id = this.id ?: "",
        name = this.name ?: "",
    )
internal val Company.toRemoteCompany: RemoteCompany
    get() = RemoteCompany(
        id = this.id,
        name = this.name,
    )
internal val Company.toRoomCompany: RoomCompany
    get() = RoomCompany(
        id = this.id,
        name = this.name,
    )

internal fun List<RoomCompany>.toCompanyList(): List<Company> = this.flatMap {
    listOf(it.toCompany)
}
