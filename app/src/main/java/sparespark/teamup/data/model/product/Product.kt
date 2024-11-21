package sparespark.teamup.data.model.product

import sparespark.teamup.data.model.CompanyEntry

data class Product(
    var id: String,
    val name: String,
    var companyEntry: CompanyEntry
)
