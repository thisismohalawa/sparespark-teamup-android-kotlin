package sparespark.teamup.transaction.itemlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import sparespark.teamup.R
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.relaunchCurrentView
import sparespark.teamup.core.setCustomImage
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.databinding.TransactionlistViewBinding
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.notedetails.NoteAdapter
import sparespark.teamup.notedetails.NoteListEvent
import sparespark.teamup.transaction.BaseTransactionView
import sparespark.teamup.transaction.itemlist.adapter.TransactionAdapter
import sparespark.teamup.transaction.itemlist.adapter.TransactionBalanceAdapter
import sparespark.teamup.transaction.itemlist.adapter.TransactionCalendarStaticsAdapter
import sparespark.teamup.transaction.itemlist.buildlogic.TransactionListInjector

class TransactionListView : BaseTransactionView(),
    ViewBindingHolder<TransactionlistViewBinding> by ViewBindingHolderImpl() {

    private lateinit var viewModel: TransactionListViewModel
    private lateinit var viewInteract: HomeActivityInteract
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var calendarStaticsAdapter: TransactionCalendarStaticsAdapter
    private lateinit var balanceAdapter: TransactionBalanceAdapter
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        initBinding(TransactionlistViewBinding.inflate(layoutInflater), this@TransactionListView) {
            setupViewInteract()
            setupViewInputs()
            setupNoteAdapter()
            setupTransactionListAdapter()
            setupStaticsListAdapter()
            setupBalanceListAdapter()
            setupViewModel()
            viewModel.setupAttachedStatesObserver()
            viewModel.setupTransactionStatesObserver()
            setupClickListener()

        }

    private fun setupViewInteract() {
        viewInteract = activity as HomeActivityInteract
    }

    private fun setupViewInputs() {
        binding?.apply {
            itemHeaderList.apply {
                itemHeader.txtTitle.text = getString(R.string.transactions)
                imgAction.setCustomImage(R.drawable.ic_settings, context)
                imgSubAction.setCustomImage(R.drawable.ic_search, context)
            }
            itemBalanceList.apply {
                itemHeader.txtTitle.text = getString(R.string.total_balance)
            }
        }
    }

    private fun setupClickListener() {
        binding?.apply {
            fabAddTransaction.setOnClickListener {
                navigateTransactionDetailsView("")
            }
            itemNote.fabAddNote.setOnClickListener {
                navigateNoteDetailsView("")
            }
            itemHeaderList.imgSubAction.setOnClickListener {
                navigateToFilterView()
            }
            itemHeaderList.imgAction.setOnClickListener {
                navigateToListPreference()
            }
            itemBalanceList.imgBalancePref.setOnClickListener {
                navigateToBalancePreference()
            }
            val callback = object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    viewInteract.finishHomeActivity()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@TransactionListView,
            factory = TransactionListInjector(requireActivity().application).provideViewModelFactory()
        )[TransactionListViewModel::class.java]
        viewModel.handleViewEvent(TransactionListEvent.OnViewStart)
        viewModel.handleAttachViewEvent(NoteListEvent.GetNoteList)

    }

    private fun setupTransactionListAdapter() {
        transactionAdapter = TransactionAdapter()
        binding?.recItemList?.apply {
            adapter = transactionAdapter
        }
        transactionAdapter.event.observe(viewLifecycleOwner) {
            viewModel.handleEvent(it)
        }
    }

    private fun setupNoteAdapter() {
        noteAdapter = NoteAdapter()
        binding?.itemNote?.recNoteList?.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = noteAdapter
        }
        noteAdapter.event.observe(viewLifecycleOwner) {
            viewModel.handleAttachViewEvent(it)
        }
    }

    private fun setupStaticsListAdapter() {
        calendarStaticsAdapter = TransactionCalendarStaticsAdapter()
        binding?.itemStaticsList?.recStaticsList?.adapter = calendarStaticsAdapter
    }

    private fun setupBalanceListAdapter() {
        balanceAdapter = TransactionBalanceAdapter(isHorizontalBalanceView = false)
        binding?.itemBalanceList?.recBalanceList?.adapter = balanceAdapter
    }

    private fun isDestination(): Boolean =
        findNavController().currentDestination?.id == R.id.transactionListView

    private fun navigateToListPreference() = if (isDestination()) findNavController().navigate(
        TransactionListViewDirections.navigateToListPreference()
    ) else Unit

    private fun navigateToBalancePreference() = if (isDestination()) findNavController().navigate(
        TransactionListViewDirections.navigateToBalancePreference()
    ) else Unit

    private fun navigateNoteDetailsView(noteId: String) =
        if (isDestination()) findNavController().navigate(
            TransactionListViewDirections.navigateToNoteDetails(
                noteId = noteId
            )
        ) else Unit

    private fun navigateTransactionDetailsView(transactionId: String) =
        if (isDestination()) findNavController().navigate(
            TransactionListViewDirections.navigateToTransactionDetails(
                itemId = transactionId
            )
        ) else Unit

    private fun navigateToFilterView(
        query: String? = null
    ) = if (isDestination()) findNavController().navigate(
        TransactionListViewDirections.navigateToItemFilter(
            searchQuery = query
        )
    ) else Unit

    private fun TransactionListViewModel.setupAttachedStatesObserver() {
        useNotesState.observe(viewLifecycleOwner) {
            binding?.itemNote?.root?.visible(it)
        }
        noteList.observe(viewLifecycleOwner) {
            noteAdapter.submitList(it)
        }
        updatedNote.observe(viewLifecycleOwner) {
            relaunchCurrentView()
        }
        editNote.observe(viewLifecycleOwner, EventObserver {
            navigateNoteDetailsView(it)
        })
    }

    private fun TransactionListViewModel.setupTransactionStatesObserver() {
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner, EventObserver {
            relaunchCurrentView()
        })
        /*============*/
        itemList.observe(viewLifecycleOwner) {
            transactionAdapter.submitList(it)
        }
        editItem.observe(viewLifecycleOwner, EventObserver {
            navigateTransactionDetailsView(it)
        })
        /*==============================*/
        updateItemListHintAttempt.observe(viewLifecycleOwner) {
            binding?.itemHeaderList?.itemHeader?.txtHint?.updateHintTitle(it)
        }
        updateBalanceHintAttempt.observe(viewLifecycleOwner) {
            binding?.itemBalanceList?.itemHeader?.txtHint?.updateHintTitle(it)
        }
        updatePositionSelectListAttempt.observe(viewLifecycleOwner) {
            binding?.recItemList?.updateSelectItem(it)
        }
        /*============================*/
        useCalendarStaticsState.observe(viewLifecycleOwner) {
            binding?.itemStaticsList?.root?.visible(it)
        }
        calendarStaticsList.observe(viewLifecycleOwner) {
            calendarStaticsAdapter.submitList(it)
        }
        /*============================*/
        useBalanceState.observe(viewLifecycleOwner) {
            binding?.itemBalanceList?.root?.visible(it)
        }
        balanceList.observe(viewLifecycleOwner) {
            balanceAdapter.submitList(it)
        }
        /*============================*/
        exported.observe(viewLifecycleOwner) {
            viewInteract.displayToast(getString(R.string.export_success))
        }
        copyItemListAttempt.observe(viewLifecycleOwner, EventObserver {
            if (it != null) viewInteract.actionCopyText(it)
        })
        shareItemListAttempt.observe(viewLifecycleOwner, EventObserver {
            if (it != null) viewInteract.actionShareText(it)
        })
    }
}