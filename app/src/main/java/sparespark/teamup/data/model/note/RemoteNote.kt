package sparespark.teamup.data.model.note

import sparespark.teamup.core.map.DEF_ITEM_ADMIN_CRUD

data class RemoteNote(
    val id: String? = "",
    val title: String? = "",
    var onlyAdmins: Boolean? = DEF_ITEM_ADMIN_CRUD,
    val creationDate: String? = "",
    val createdBy: String? = ""
)