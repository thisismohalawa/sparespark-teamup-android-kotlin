package sparespark.teamup.data.repository

import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.company.Company

interface CompanyRepository {
    suspend fun getCompanyList(localOnly: Unit? = null): Result<Exception, List<Company>>
    suspend fun updateCompany(company: Company): Result<Exception, Unit>
    suspend fun deleteCompany(
        itemId: String? = null,
        itemsIds: List<String>? = null,
    ): Result<Exception, Unit>

    suspend fun clearListCacheTime(): Result<Exception, Unit>

}
