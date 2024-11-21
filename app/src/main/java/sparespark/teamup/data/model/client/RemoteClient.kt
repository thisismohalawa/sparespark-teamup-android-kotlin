package sparespark.teamup.data.model.client

import sparespark.teamup.data.model.LocationEntry

data class RemoteClient(
    val id: String? = "",
    val name: String? = "",
    val phone: String? = "",
    val locationEntry: LocationEntry? = LocationEntry("", "")
)
