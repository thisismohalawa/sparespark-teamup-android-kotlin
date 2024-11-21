package sparespark.teamup.transaction

import sparespark.teamup.core.base.BaseAdministrationViewModel
import sparespark.teamup.data.repository.CityRepository
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.PreferenceRepository

abstract class BaseTransactionListEventViewModel<DE, VE, AVE>(
    cityRepository: CityRepository?,
    clientRepository: ClientRepository?,
    preferenceRepository: PreferenceRepository?
) : BaseAdministrationViewModel<DE>(
    cityRepo = cityRepository,
    clientRepo = clientRepository,
    preferenceRepo = preferenceRepository,
    companyRepo = null,
    productRepo = null
) {

    abstract fun handleViewEvent(event: VE)

    abstract fun handleAttachViewEvent(event: AVE)

}