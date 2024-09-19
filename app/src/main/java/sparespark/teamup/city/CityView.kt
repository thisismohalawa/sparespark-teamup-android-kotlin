package sparespark.teamup.city

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
import sparespark.teamup.city.buildlogic.CityViewInjector
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.isMatch
import sparespark.teamup.core.relaunchCurrentView
import sparespark.teamup.core.setupListItemDecoration
import sparespark.teamup.core.toEditable
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.data.model.city.City
import sparespark.teamup.databinding.ExpandedListViewBinding
import sparespark.teamup.home.HomeViewInteract
import sparespark.teamup.home.base.BaseViewBehavioral

class CityView : Fragment(),
    BaseViewBehavioral,
    View.OnClickListener,
    ViewBindingHolder<ExpandedListViewBinding> by ViewBindingHolderImpl() {

    private lateinit var cityAdapter: CityAdapter
    private lateinit var viewModel: CityViewModel
    private lateinit var viewInteract: HomeViewInteract
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(ExpandedListViewBinding.inflate(layoutInflater), this@CityView) {
        setupBottomSheetState()
        setupViewInteract()
        setupViewInputs()
        setupListAdapter()
        setupViewModel()
        viewModel.setupStatesObserver()
        setupClickListener()
    }

    private fun setupBottomSheetState() {
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
            edTitle.hint = getString(R.string.name) + getString(R.string.required)
            itemSearch.mtSearchView.queryHint = getString(R.string.search_for_city)
            itemShareExpense.content.visible(false)
            itemShareClient.content.visible(false)
        }
    }

    override fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@CityView,
            factory = CityViewInjector(requireActivity().application).provideViewModelFactory()
        )[CityViewModel::class.java]
        viewModel.handleEvent(CityEvent.GetCityList)
        viewModel.handleEvent(CityEvent.OnStartGetCity)
    }

    override fun setupClickListener() {
        binding?.txtUpdate?.setOnClickListener(this@CityView)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) viewModel.handleEvent(
                CityEvent.HideBottomSheet
            )
            else findNavController().popBackStack()
        }
    }

    private fun setupListAdapter() {
        cityAdapter = CityAdapter()
        binding?.itemRecList?.apply {
            adapter = cityAdapter
            setupListItemDecoration(context)
        }
        cityAdapter.event.observe(
            viewLifecycleOwner
        ) {
            viewModel.handleEvent(it)
        }
    }

    private fun setupSearchViewListener(list: List<City>) {
        binding?.itemSearch?.mtSearchView?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filteredList = mutableListOf<City>()
                    for (city: City in list) if (city.name.isMatch(it)) filteredList.add(city)

                    if (filteredList.isNotEmpty()) cityAdapter.submitList(filteredList)
                }
                return true
            }
        })
    }

    private fun CityViewModel.setupStatesObserver() {
        loading.observe(viewLifecycleOwner) {
            viewInteract.updateProgressLoad(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner) {
            relaunchCurrentView()
        }
        bottomSheetViewState.observe(viewLifecycleOwner) {
            bottomSheetBehavior.state = it
        }
        city.observe(viewLifecycleOwner) {
            binding?.edTitle?.text = it.name.toEditable()
        }
        cityList.observe(viewLifecycleOwner) {
            cityAdapter.submitList(it)
            setupSearchViewListener(it)
        }
        filterCityAttempt.observe(viewLifecycleOwner, EventObserver {
            if (findNavController().currentDestination?.id == R.id.cityView) findNavController().navigate(
                CityViewDirections.navigateToFilterList(
                    searchQuery = it
                )
            )
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.txt_update) viewModel.handleEvent(
            CityEvent.OnUpdateTxtClick(
                city = binding?.edTitle?.text?.trim().toString()
            )
        )
    }
}