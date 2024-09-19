package sparespark.teamup.expense

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sparespark.teamup.R
import sparespark.teamup.core.DELAY_VIEW_EXPAND
import sparespark.teamup.core.MAX_ASSET_DIG
import sparespark.teamup.core.START_EXPENSE_EXPORT
import sparespark.teamup.core.getCalendarDateTime
import sparespark.teamup.core.getSystemTimeMillis
import sparespark.teamup.core.isNewIdItem
import sparespark.teamup.core.isValidNumFormated
import sparespark.teamup.core.limitDouble
import sparespark.teamup.core.map.DEF_EXP_INCOME
import sparespark.teamup.core.map.DEF_EXP_TEAM
import sparespark.teamup.core.wrapper.BaseViewModel
import sparespark.teamup.core.wrapper.Event
import sparespark.teamup.core.wrapper.Result
import sparespark.teamup.data.model.client.Client
import sparespark.teamup.data.model.expense.Expense
import sparespark.teamup.data.model.team.Team
import sparespark.teamup.data.repository.ClientRepository
import sparespark.teamup.data.repository.ExpenseRepository
import sparespark.teamup.data.repository.TeamRepository

class ExpenseViewModel(
    private val expenseRepo: ExpenseRepository,
    private val clientRepo: ClientRepository,
    private val teamRepo: TeamRepository
) : BaseViewModel<ExpenseEvent>() {

    internal val teamState = MutableLiveData<Boolean>()
    internal val incomeState = MutableLiveData<Boolean>()
    internal val bottomSheetViewState = MutableLiveData<Int>()
    internal val clearSharedNameAttempt = MutableLiveData<Unit>()
    internal val updateTotalCostTextAttempt = MutableLiveData<Double>()
    internal val exportExpenseListAttempt = MutableLiveData<Event<String>>()

    private val expenseState = MutableLiveData<Expense>()
    val expense: LiveData<Expense> get() = expenseState

    private val expenseListState = MutableLiveData<List<Expense>>()
    val expenseList: LiveData<List<Expense>> get() = expenseListState

    private val clientListState = MutableLiveData<List<Client>?>()
    val clientList: LiveData<List<Client>?> get() = clientListState

    private val teamListState = MutableLiveData<List<Team>?>()
    val teamList: LiveData<List<Team>?> get() = teamListState

    private val updatedState = MutableLiveData<Unit>()
    val updated: LiveData<Unit> get() = updatedState

    override fun handleEvent(event: ExpenseEvent) {
        when (event) {
            is ExpenseEvent.OnStartGetItem -> setupExpense(pos = null)
            is ExpenseEvent.OnListItemClick -> setupExpense(pos = event.pos)
            is ExpenseEvent.GetExpenseList -> getExpenseList(resultAction = {
                calculateTotalCost()
            })

            is ExpenseEvent.OnSwitchIncomeUpdate -> incomeState.value = event.income
            is ExpenseEvent.OnSwitchTeamUpdate -> {
                clearShareName()
                teamState.value = event.team
                checkIfTeamClientShared(event.team)
            }

            is ExpenseEvent.OnAutoCompleteNameSelect -> updateSharedName(event.name)
            is ExpenseEvent.HideBottomSheet -> hideBottomSheet()
            is ExpenseEvent.OnMenuListRefresh -> clearListCacheTime()
            is ExpenseEvent.OnMenuExportClick -> exportExpenseListAttempt.value =
                Event(START_EXPENSE_EXPORT)

            is ExpenseEvent.OnMenuDeleteClick -> deleteItem(event.pos)
            is ExpenseEvent.OnUpdateTxtClick -> updateExpense(event.name, event.cost, event.note)

        }
    }

    private fun setupExpense(pos: Int?) = viewModelScope.launch {
        if (pos == null) expenseState.value =
            Expense("", "", "", "", 0.0, "", income = DEF_EXP_INCOME, team = DEF_EXP_TEAM)
        else expenseState.value = expenseListState.value?.get(pos)

        delay(DELAY_VIEW_EXPAND)
        expandBottomSheet()
        updateExpenseState()
    }

    private fun getLocalList() = viewModelScope.launch {
        val result = expenseRepo.getExpenseList(localOnly = Unit)
        if (result is Result.Value)
            expenseListState.value = result.value.asReversed()
        else showError(R.string.cannot_read_local_data)
    }

    private fun getExpenseList(resultAction: (() -> Unit)? = null) = viewModelScope.launch {
        showLoading()
        when (val result = expenseRepo.getExpenseList()) {
            is Result.Error -> result.error.message.actionExceptionMsg(
                offline = {
                    getLocalList()
                },
                deactivated = { Unit },
                notPermitted = { Unit },
                error = {
                    showError(R.string.error_retrieve_data)
                }
            )

            is Result.Value -> {
                expenseListState.value = result.value.asReversed()
                resultAction?.invoke()
            }
        }
        hideLoading()
    }

    private fun calculateTotalCost() = viewModelScope.launch {
        val result = expenseList.value?.let { expenseRepo.calculateTotalExpenses(list = it) }
        if (result is Result.Value) updateTotalCostTextAttempt.value = result.value
        else showError(R.string.cannot_read_statistics)
    }

    private fun updateExpense(name: String, cost: String, note: String) = viewModelScope.launch {
        if (isInputValid(cost))
            expenseState.value?.let {
                showLoading()
                if (it.id.isNewIdItem()) {
                    it.id = getSystemTimeMillis()
                    it.creationDate = getCalendarDateTime()
                }

                when (val result = expenseRepo.updateExpense(
                    if (it.name.isBlank()) {
                        expenseState.value!!.copy(
                            name = name,
                            cost = cost.toDouble().limitDouble(),
                            note = note,
                            income = incomeState.value ?: DEF_EXP_INCOME,
                            team = teamState.value ?: DEF_EXP_TEAM,
                        )

                    } else expenseState.value!!.copy(
                        cost = cost.toDouble().limitDouble(),
                        note = note,
                        income = incomeState.value ?: DEF_EXP_INCOME,
                        team = teamState.value ?: DEF_EXP_TEAM,
                    )
                )) {
                    is Result.Error -> result.error.message.checkExceptionMsg(error = {
                        showError(R.string.cannot_update_entries)
                    })

                    is Result.Value -> updatedState.value = Unit
                }
            }
        hideLoading()
    }

    private fun deleteItem(pos: Int) = viewModelScope.launch {
        showLoading()
        when (val result = expenseListState.value?.get(pos)?.id?.let {
            expenseRepo.deleteExpense(
                id = it
            )
        }) {
            is Result.Error -> result.error.message.checkExceptionMsg(error = {
                showError(R.string.cannot_update_entries)
            })

            is Result.Value -> updatedState.value = Unit
            else -> showError(R.string.cannot_read_local_data)
        }
        hideLoading()
    }

    private fun clearListCacheTime() = viewModelScope.launch {
        showLoading()
        if (expenseRepo.clearListCacheTime() is Result.Value) updatedState.value = Unit
        else showError(R.string.cannot_update_local_entries)
        hideLoading()
    }

    private fun getTeam() = viewModelScope.launch {
        showLoading()
        when (val result = teamRepo.getTeamList()) {
            is Result.Error -> result.error.message.checkExceptionMsg(
                error = {
                    showError(R.string.error_retrieve_team)
                }
            )

            is Result.Value -> teamListState.value = result.value
        }
        hideLoading()
    }

    private fun getClientList() = viewModelScope.launch {
        showLoading()
        when (val result = clientRepo.getClientList()) {
            is Result.Error -> result.error.message.checkExceptionMsg(
                error = {
                    showError(R.string.error_retrieve_clientlist)
                }
            )

            is Result.Value -> clientListState.value = result.value
        }
        hideLoading()
    }

    private fun updateExpenseState() {
        expenseState.value?.let {
            incomeState.value = it.income
            teamState.value = it.team
            checkIfTeamClientShared(it.team)
        }
    }

    private fun checkIfTeamClientShared(team: Boolean) {
        if (team) {
            clientListState.value = null
            getTeam()
        } else {
            teamListState.value = null
            getClientList()
        }
    }

    private fun updateSharedName(name: String) {
        expenseState.value?.name = name
    }

    private fun clearShareName() {
        updateSharedName("")
        clearSharedNameAttempt.value = Unit
    }

    private fun expandBottomSheet() {
        bottomSheetViewState.value = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideBottomSheet() {
        bottomSheetViewState.value = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun isInputValid(cost: String): Boolean = if (!cost.isValidNumFormated(MAX_ASSET_DIG)) {
        showError(R.string.invalid_cost)
        false
    } else true

}