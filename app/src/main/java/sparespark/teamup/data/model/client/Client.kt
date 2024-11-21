package sparespark.teamup.data.model.client

import sparespark.teamup.data.model.LocationEntry

data class Client(
    var id: String,
    var name: String,
    var phone: String,
    var locationEntry: LocationEntry
)
