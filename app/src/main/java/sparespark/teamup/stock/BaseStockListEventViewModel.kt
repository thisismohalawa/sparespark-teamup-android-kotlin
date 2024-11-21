package sparespark.teamup.stock

import sparespark.teamup.core.base.BaseAdministrationViewModel
import sparespark.teamup.data.repository.CityRepository
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.CompanyRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.ProductRepository

abstract class BaseStockListEventViewModel<DE, VE>(
    cityRepository: CityRepository?,
    clientRepository: ClientRepository?,
    companyRepository: CompanyRepository?,
    productRepository: ProductRepository?,
    preferenceRepo: PreferenceRepository?
) : BaseAdministrationViewModel<DE>(
    cityRepo = cityRepository,
    clientRepo = clientRepository,
    companyRepo = companyRepository,
    productRepo = productRepository,
    preferenceRepo = preferenceRepo
) {

    abstract fun handleViewEvent(event: VE)

}