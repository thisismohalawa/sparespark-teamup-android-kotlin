package sparespark.teamup.data.model.client

data class RemoteClient(
    val id: String? = "",
    val name: String? = "",
    val phone: String? = "",
    val locationEntry: LocationEntry? = LocationEntry("", "")
)
