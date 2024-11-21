package sparespark.teamup.data.model.product

import sparespark.teamup.data.model.CompanyEntry

data class RemoteProduct(
    val id: String? = "",
    var name: String? = "",
    var companyEntry: CompanyEntry? = CompanyEntry("", "")
)
