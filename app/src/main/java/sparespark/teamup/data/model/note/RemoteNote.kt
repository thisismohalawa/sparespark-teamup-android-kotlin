package sparespark.teamup.data.model.note

data class RemoteNote(
    val id: String? = "",
    val title: String? = "",
    var onlyAdmins: Boolean? = false,
    val creationDate: String? = "",
    val createdBy: String? = ""
)