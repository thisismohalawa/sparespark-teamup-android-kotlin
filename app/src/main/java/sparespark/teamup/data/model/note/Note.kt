package sparespark.teamup.data.model.note

data class Note(
    var id: String,
    val title: String,
    var onlyAdmins: Boolean,
    var creationDate: String,
    var createdBy: String
)

