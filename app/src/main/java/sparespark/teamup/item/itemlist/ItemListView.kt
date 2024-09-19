package sparespark.teamup.item.itemlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import sparespark.teamup.R
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.getCalendarDateTime
import sparespark.teamup.core.getCalendarSearchDay
import sparespark.teamup.core.relaunchCurrentView
import sparespark.teamup.core.setCustomColor
import sparespark.teamup.core.toNumString
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.data.model.statics.CStatics
import sparespark.teamup.databinding.ItemlistViewBinding
import sparespark.teamup.home.HomeViewInteract
import sparespark.teamup.home.base.BaseViewBehavioral
import sparespark.teamup.item.BaseItemsView
import sparespark.teamup.item.adapter.ItemAdapter
import sparespark.teamup.item.itemlist.buildlogic.ItemListInjector
import sparespark.teamup.note.NoteListEvent
import sparespark.teamup.note.adapter.NoteAdapter

class ItemListView : BaseItemsView(),
    BaseViewBehavioral,
    ViewBindingHolder<ItemlistViewBinding> by ViewBindingHolderImpl() {

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var viewModel: ItemListViewModel
    private lateinit var viewInteract: HomeViewInteract

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(ItemlistViewBinding.inflate(layoutInflater), this@ItemListView) {
        setupViewInteract()
        setupViewInputs()
        setupListAdapter()
        setupNoteAdapter()
        setupViewModel()
        viewModel.setupItemStatesObserver()
        viewModel.setupAttachedStatesObserver()
        setupClickListener()
    }

    override fun setupViewInteract() {
        viewInteract = activity as HomeViewInteract
    }

    override fun setupViewInputs() {
        binding?.apply {
            itemListHeader.itemHeader.txtTitle.text = getString(R.string.exchange_list)
            itemStatics.apply {
                itemBalance.txtTitle.text = getString(R.string.total_balance)
                itemBalance.txtTitle.setCustomColor(
                    R.color.dark, context
                )
            }
            itemCalender.apply {
                itemYesterday.txtDes.text = getString(R.string.yesterday)
                itemToday.txtDes.text = getString(R.string.today)
                itemActive.txtDes.text = getString(R.string.uncompleted)
            }
        }
    }

    override fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@ItemListView,
            factory = ItemListInjector(requireActivity().application).provideViewModelFactory()
        )[ItemListViewModel::class.java]
        viewModel.handleViewEvent(ItemListEvent.OnViewStart)
        viewModel.handleAttachViewEvent(NoteListEvent.GetNoteList)
    }

    override fun setupClickListener() {
        binding?.apply {
            itemNoteList.fabAddNote.setOnClickListener {
                navigateNoteDetailsView("")
            }
            itemStatics.imgPref.setOnClickListener {
                if (isDestination()) findNavController().navigate(
                    ItemListViewDirections.navigateToStaticsPreference()
                ) else Unit
            }
            itemListHeader.imgPref.setOnClickListener {
                if (isDestination()) findNavController().navigate(
                    ItemListViewDirections.navigateToListPreference()
                ) else Unit
            }
            itemCalender.apply {
                val yesterdayViewListener = View.OnClickListener { _ ->
                    itemYesterday.txtDes.inflateCalenderYesterdayMenu()
                }
                val todayViewListener = View.OnClickListener { _ ->
                    itemToday.txtDes.inflateCalenderTodayMenu()
                }
                val activeViewListener = View.OnClickListener { _ ->
                    itemActive.txtDes.inflateCalenderActiveMenu()
                }
                itemYesterday.txtTitle.setOnClickListener(yesterdayViewListener)
                itemYesterday.txtDes.setOnClickListener(yesterdayViewListener)
                itemActive.txtTitle.setOnClickListener(activeViewListener)
                itemActive.txtDes.setOnClickListener(activeViewListener)
                itemToday.txtTitle.setOnClickListener(todayViewListener)
                itemToday.txtDes.setOnClickListener(todayViewListener)
            }
            val callback = object : OnBackPressedCallback(
                true
            ) {
                override fun handleOnBackPressed() {
                    viewModel.handleViewEvent(ItemListEvent.OnViewBackPressed)
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        }
    }

    override fun setupListAdapter() {
        itemAdapter = ItemAdapter()
        binding?.recItemsList?.apply {
            adapter = itemAdapter
        }
        itemAdapter.event.observe(
            viewLifecycleOwner
        ) {
            viewModel.handleEvent(it)
        }
    }

    private fun setupNoteAdapter() {
        noteAdapter = NoteAdapter()
        binding?.itemNoteList?.recNoteList?.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = noteAdapter
        }
        noteAdapter.event.observe(
            viewLifecycleOwner
        ) {
            viewModel.handleAttachViewEvent(it)
        }
    }

    private fun View.inflateCalenderYesterdayMenu() {
        val popupMenu = PopupMenu(this@inflateCalenderYesterdayMenu.context, this)
        popupMenu.apply {
            inflate(R.menu.calender_yesterday_menu)
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.menu_active -> navigateToFilterView(
                        query = getCalendarSearchDay(day = -1), active = true
                    )

                    R.id.menu_buy -> navigateToFilterView(
                        query = getCalendarSearchDay(day = -1), buy = true
                    )

                    R.id.menu_admin -> navigateToFilterView(
                        query = getCalendarSearchDay(day = -1), admin = true
                    )

                    R.id.menu_view_all -> navigateToFilterView(
                        query = getCalendarSearchDay(day = -1)
                    )
                }
                false
            }
            show()
        }
    }

    private fun View.inflateCalenderActiveMenu() {
        val popupMenu = PopupMenu(this@inflateCalenderActiveMenu.context, this)
        popupMenu.apply {
            inflate(R.menu.calender_active_menu)
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.menu_active -> navigateToFilterView(
                        active = true
                    )
                }
                false
            }
            show()
        }
    }

    private fun View.inflateCalenderTodayMenu() {
        val popupMenu = PopupMenu(this@inflateCalenderTodayMenu.context, this)
        popupMenu.apply {
            inflate(R.menu.calender_today_menu)
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.menu_active -> navigateToFilterView(
                        query = getCalendarSearchDay(), active = true
                    )

                    R.id.menu_buy -> navigateToFilterView(
                        query = getCalendarSearchDay(), buy = true
                    )

                    R.id.menu_admin -> navigateToFilterView(
                        query = getCalendarSearchDay(), admin = true
                    )
                }
                false
            }
            show()
        }
    }

    private fun isDestination(): Boolean =
        findNavController().currentDestination?.id == R.id.itemListView

    private fun navigateToItemDetailsView(itemXId: String) =
        if (isDestination()) findNavController().navigate(
            ItemListViewDirections.navigateToItemDetails(itemXId)
        ) else Unit

    private fun navigateNoteDetailsView(noteId: String) =
        if (isDestination()) findNavController().navigate(
            ItemListViewDirections.navigateToNoteDetails(noteId)
        ) else Unit

    private fun navigateToFilterView(
        query: String? = null, active: Boolean = false, admin: Boolean = false, buy: Boolean = false
    ) = if (isDestination()) findNavController().navigate(
        ItemListViewDirections.navigateToFilteredList(
            searchQuery = query, activeItems = active, adminItems = admin, buyItems = buy
        )
    ) else Unit

    private fun ItemListViewModel.setupAttachedStatesObserver() {
        useCalendarStaticsState.observe(viewLifecycleOwner) {
            binding?.itemCalender?.content?.visible(it)
        }
        useListStaticsState.observe(viewLifecycleOwner) {
            binding?.itemStatics?.content?.visible(it)
        }
        useNotesState.observe(viewLifecycleOwner) {
            binding?.itemNoteList?.content?.visible(isVisible = it)
        }
        calenderStatics.observe(viewLifecycleOwner) {
            updateCalenderStatics(it)
        }
        listStatics.observe(viewLifecycleOwner) {
            binding?.itemStatics?.updateListStatics(it)
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

    private fun ItemListViewModel.setupItemStatesObserver() {
        exitState.observe(viewLifecycleOwner) {
            viewInteract.finishHomeActivity()
        }
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        editItem.observe(viewLifecycleOwner, EventObserver {
            navigateToItemDetailsView(it)
        })
        updateItemListHintAttempt.observe(viewLifecycleOwner) {
            binding?.itemListHeader?.itemHeader?.txtHint?.updateHintTitle(it)
        }
        updateItemSelectAttempt.observe(viewLifecycleOwner) {
            binding?.recItemsList?.updateSelectItem(it)
        }
        updateItemListSelectAttempt.observe(viewLifecycleOwner) {
            unselectItems(it)
        }
        updatedItem.observe(viewLifecycleOwner) {
            relaunchCurrentView()
        }
        itemList.observe(viewLifecycleOwner) {
            itemAdapter.submitList(it)
        }
        copyItemListAttempt.observe(viewLifecycleOwner, EventObserver {
            if (it != null) viewInteract.actionCopyText(it)
        })
        shareItemListAttempt.observe(viewLifecycleOwner, EventObserver {
            if (it != null) viewInteract.actionShareText(it)
        })
        exportItemListAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.actionDataExport(it)
        })
    }

    private fun unselectItems(it: List<Int>?) {
        it?.forEach {
            val itemView = binding?.recItemsList?.findViewHolderForAdapterPosition(it)?.itemView
            val contentLayout = itemView?.findViewById<LinearLayout>(R.id.content_layout)
            contentLayout?.setBackgroundResource(R.drawable.item_rounded_layout_gray)
        }
    }

    private fun updateCalenderStatics(it: CStatics) {
        binding?.itemCalender?.apply {
            itemYesterday.txtTitle.text = it.totalYesterday.toNumString()
            itemToday.txtTitle.text = it.totalToday.toNumString()
            itemActive.txtTitle.text = it.totalUnCompleted.toNumString()
        }
    }
}