package sparespark.teamup.transaction.itemlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.balance.TransactionBalance
import sparespark.teamup.data.model.statics.TransactionCalendarStatics
import sparespark.teamup.data.repository.NoteRepository
import sparespark.teamup.data.repository.PreferenceRepository
import sparespark.teamup.data.repository.TransactionRepository
import sparespark.teamup.notedetails.NoteListEvent

class TransactionListViewModel(
    transactionRepository: TransactionRepository,
    preferenceRepository: PreferenceRepository?,
    noteRepository: NoteRepository
) : BaseTransactionListNoteViewModel<TransactionListEvent, NoteListEvent>(
    transactionRepository = transactionRepository,
    cityRepository = null,
    clientRepository = null,
    preferenceRepository = preferenceRepository,
    noteRepository = noteRepository
) {
    internal val useCalendarStaticsState = MutableLiveData<Boolean>()
    internal val updateBalanceHintAttempt = MutableLiveData<List<Int>>()

    private val calendarStaticsListState = MutableLiveData<List<TransactionCalendarStatics>>()
    val calendarStaticsList: LiveData<List<TransactionCalendarStatics>> get() = calendarStaticsListState

    override fun handleAttachViewEvent(event: NoteListEvent) {
        when (event) {
            is NoteListEvent.GetNoteList -> updateNoteUse()
            is NoteListEvent.OnMenuNoteListRefresh -> clearNoteListCacheTime()
            is NoteListEvent.OnNoteItemClick -> editNote(event.pos)
            is NoteListEvent.OnMenuNoteListDelete -> deleteNote(event.pos)
        }
    }

    override fun handleViewEvent(event: TransactionListEvent) {
        when (event) {
            TransactionListEvent.OnViewStart -> {
                useCalendarStaticsState.value = isStaticsEnable()
                useBalanceState.value = isBalanceEnable()
                getItemList(resultAction = {
                    getTransactionCalendarStatics()
                    getTransactionBalance()
                }, errorAction = {
                    useCalendarStaticsState.value = false
                    useBalanceState.value = false
                })
                updateItemListHintTitle()
                updateBalanceListHintTitle()
            }
        }
    }

    private fun updateItemListHintTitle() = viewModelScope.launch {
        val result = transactionRepository.getItemListHintTitle()
        if (result is Result.Value) updateItemListHintAttempt.value = result.value
    }

    private fun updateBalanceListHintTitle() = viewModelScope.launch {
        if (useBalanceState.value == false) return@launch
        val result = transactionRepository.getBalanceListHintTitle()
        if (result is Result.Value) updateBalanceHintAttempt.value = result.value
    }

    private fun getTransactionCalendarStatics() = viewModelScope.launch {
        if (useCalendarStaticsState.value == false) return@launch
        when (val result = transactionRepository.getCalendarListStatics()) {
            is Result.Error -> result.error.message.actionExceptionMsg(
                offline = {

                }, unauthorised = {

                }, deactivated = {
                    calendarStaticsListState.value = listOf(
                        TransactionCalendarStatics(
                            totalCount = 0,
                            titleRes = R.string.deactivated
                        )
                    )
                }, notPermitted = {
                    calendarStaticsListState.value = listOf(
                        TransactionCalendarStatics(
                            totalCount = 0,
                            titleRes = R.string.disable
                        )
                    )
                }, error = {
                    showError(R.string.cannot_read_statistics)
                })

            is Result.Value -> calendarStaticsListState.value = result.value
        }
    }

    private fun getTransactionBalance() = viewModelScope.launch {
        if (useBalanceState.value == false) return@launch
        when (val result = transactionRepository.getTransactionBalanceList()) {
            is Result.Error -> result.error.message.actionExceptionMsg(
                offline = {
                    updateBalanceHintAttempt.value = listOf(R.string.no_internet)
                }, unauthorised = {
                    updateBalanceHintAttempt.value = listOf(R.string.unauthorized)
                }, deactivated = {
                    updateBalanceHintAttempt.value = listOf(R.string.deactivated)
                }, notPermitted = {
                    updateBalanceHintAttempt.value = listOf(R.string.disable)
                }, error = {
                    showError(R.string.error_retrieve_data)
                })

            is Result.Value -> balanceListState.value = result.value
        }
    }

}