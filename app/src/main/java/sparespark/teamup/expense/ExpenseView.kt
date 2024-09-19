package sparespark.teamup.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import sparespark.teamup.R
import sparespark.teamup.core.bindClients
import sparespark.teamup.core.bindTeam
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.isMatch
import sparespark.teamup.core.isNewIdItem
import sparespark.teamup.core.plusCurrency
import sparespark.teamup.core.relaunchCurrentView
import sparespark.teamup.core.setCustomColor
import sparespark.teamup.core.setNumberDecimalInput
import sparespark.teamup.core.setupListItemDecoration
import sparespark.teamup.core.toEditable
import sparespark.teamup.core.toFormatedString
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.data.model.expense.Expense
import sparespark.teamup.databinding.ExpandedListViewBinding
import sparespark.teamup.expense.buildlogic.ExpenseInjector
import sparespark.teamup.home.HomeViewInteract
import sparespark.teamup.home.base.BaseViewBehavioral

class ExpenseView : Fragment(), BaseViewBehavioral, View.OnClickListener,
    ViewBindingHolder<ExpandedListViewBinding> by ViewBindingHolderImpl() {

    private lateinit var listAdapter: ExpenseAdapter
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var viewInteract: HomeViewInteract
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(ExpandedListViewBinding.inflate(layoutInflater), this@ExpenseView) {
        setupBottomSheet()
        setupViewInteract()
        setupViewInputs()
        setupAdapter()
        setupViewModel()
        viewModel.setupStatesObserver()
        setupClickListener()
    }

    private fun setupBottomSheet() {
        binding?.apply {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetSubAction)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun setupViewInteract() {
        viewInteract = activity as HomeViewInteract
    }

    override fun setupViewInputs() {
        binding?.apply {
            edTitle.hint = getString(R.string.cost) + getString(R.string.required)
            updateSearchHint(0.0)
            edTitle.setNumberDecimalInput()
            itemShareExpense.content.visible(true)
            itemShareClient.content.visible(false)
        }
    }

    override fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@ExpenseView,
            factory = ExpenseInjector(requireActivity().application).provideViewModelFactory()
        )[ExpenseViewModel::class.java]
        viewModel.handleEvent(ExpenseEvent.GetExpenseList)
        viewModel.handleEvent(ExpenseEvent.OnStartGetItem)
    }

    override fun setupClickListener() {
        binding?.apply {
            txtUpdate.setOnClickListener(this@ExpenseView)
            itemShareExpense.apply {
                switchIncome.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView?.isPressed == true) viewModel.handleEvent(
                        ExpenseEvent.OnSwitchIncomeUpdate(
                            income = isChecked
                        )
                    )

                }
                switchTeam.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView?.isPressed == true) viewModel.handleEvent(
                        ExpenseEvent.OnSwitchTeamUpdate(
                            team = isChecked
                        )
                    )
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this@ExpenseView) {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) viewModel.handleEvent(
                ExpenseEvent.HideBottomSheet
            )
            else findNavController().popBackStack()
        }
    }

    private fun setupAdapter() {
        listAdapter = ExpenseAdapter()
        binding?.itemRecList?.apply {
            adapter = listAdapter
            setupListItemDecoration(context)
        }
        listAdapter.event.observe(
            viewLifecycleOwner
        ) {
            viewModel.handleEvent(it)
        }
    }

    private fun setupSearchViewListener(list: List<Expense>) {
        binding?.itemSearch?.mtSearchView?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filteredList = mutableListOf<Expense>()
                    for (item: Expense in list) if (item.name.isMatch(it)) filteredList.add(item)
                    else if (item.creationDate.isMatch(it)) filteredList.add(item)
                    else if (item.note.isMatch(it)) filteredList.add(item)
                    else if (item.cost.toString().contains(it)) filteredList.add(item)

                    if (filteredList.isNotEmpty()) listAdapter.submitList(filteredList)
                }
                return true
            }
        })
    }

    private fun updateSearchHint(tCost: Double) {
        binding?.apply {
            itemSearch.mtSearchView.queryHint = getString(R.string.total) + "= ${
                tCost.toFormatedString().plusCurrency(context)
            }, " + getString(R.string.search_expense)
        }
    }

    private fun updateIncomeView(it: Boolean) {
        binding?.apply {
            itemShareExpense.switchIncome.isChecked = it
            if (it) txtUpdate.setCustomColor(R.color.green, context)
            else txtUpdate.setCustomColor(R.color.red, context)
        }
    }

    private fun bindExpense(expense: Expense) {
        binding?.apply {
            edTitle.text = expense.cost.toString().toEditable()
            itemShareExpense.edName.text = expense.name.toEditable()
            itemShareExpense.edNote.text = expense.note.toEditable()
        }
    }

    private fun ExpenseViewModel.setupStatesObserver() {
        bottomSheetViewState.observe(viewLifecycleOwner) {
            bottomSheetBehavior.state = it
        }
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner) {
            relaunchCurrentView()
        }
        updateTotalCostTextAttempt.observe(viewLifecycleOwner) {
            updateSearchHint(it)
        }
        clearSharedNameAttempt.observe(viewLifecycleOwner) {
            binding?.itemShareExpense?.edName?.text = null
        }
        teamState.observe(viewLifecycleOwner) {
            binding?.itemShareExpense?.switchTeam?.isChecked = it
        }
        incomeState.observe(viewLifecycleOwner) {
            updateIncomeView(it)
        }
        exportExpenseListAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.actionDataExport(it)
        })
        expense.observe(viewLifecycleOwner) {
            if (!it.id.isNewIdItem()) bindExpense(it)
        }
        expenseList.observe(viewLifecycleOwner) { list ->
            listAdapter.submitList(list)
            setupSearchViewListener(list)
        }
        clientList.observe(viewLifecycleOwner) {
            it?.let { list ->
                binding?.itemShareExpense?.edName?.bindClients(list = list, action = { name ->
                    handleEvent(
                        ExpenseEvent.OnAutoCompleteNameSelect(
                            name = name
                        )
                    )
                })
            }
        }
        teamList.observe(viewLifecycleOwner) {
            it?.let { list ->
                binding?.itemShareExpense?.edName?.bindTeam(list = list, action = { pos ->
                    handleEvent(
                        ExpenseEvent.OnAutoCompleteNameSelect(
                            pos
                        )
                    )
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.txt_update) viewModel.handleEvent(
            ExpenseEvent.OnUpdateTxtClick(
                cost = binding?.edTitle?.text?.trim().toString(),
                name = binding?.itemShareExpense?.edName?.text?.trim().toString(),
                note = binding?.itemShareExpense?.edNote?.text?.trim().toString()
            )
        )
    }
}