package sparespark.teamup.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import sparespark.teamup.R
import sparespark.teamup.client.buildlogic.ClientViewInjector
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.isMatch
import sparespark.teamup.core.relaunchCurrentView
import sparespark.teamup.core.setPhoneNumInput
import sparespark.teamup.core.setupListItemDecoration
import sparespark.teamup.core.toEditable
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.data.model.city.City
import sparespark.teamup.data.model.client.Client
import sparespark.teamup.databinding.ExpandedListViewBinding
import sparespark.teamup.home.HomeViewInteract
import sparespark.teamup.home.base.BaseViewBehavioral

class ClientView : Fragment(),
    BaseViewBehavioral, View.OnClickListener,
    ViewBindingHolder<ExpandedListViewBinding> by ViewBindingHolderImpl() {

    private lateinit var clientAdapter: ClientAdapter
    private lateinit var viewModel: ClientViewModel
    private lateinit var viewInteract: HomeViewInteract
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(ExpandedListViewBinding.inflate(layoutInflater), this@ClientView) {
        setupBottomSheet()
        setupViewInteract()
        setupViewInputs()
        setupListAdapter()
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
            edTitle.hint = getString(R.string.name) + getString(R.string.required)
            itemSearch.mtSearchView.queryHint = getString(R.string.search_clients)
            itemShareExpense.content.visible(false)
            itemShareClient.apply {
                edPhone.setPhoneNumInput()
                content.visible(isVisible = true)
            }
        }
    }

    override fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@ClientView,
            factory = ClientViewInjector(requireActivity().application).provideViewModelFactory()
        )[ClientViewModel::class.java]
        viewModel.handleEvent(ClientEvent.OnStartGetClient)
        viewModel.handleEvent(ClientEvent.GetClientList)
    }

    override fun setupClickListener() {
        binding?.txtUpdate?.setOnClickListener(this@ClientView)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) viewModel.handleEvent(
                ClientEvent.HideBottomSheet
            )
            else findNavController().popBackStack()
        }
    }

    private fun setupListAdapter() {
        clientAdapter = ClientAdapter()
        binding?.itemRecList?.apply {
            adapter = clientAdapter
            setupListItemDecoration(context)
        }
        clientAdapter.event.observe(
            viewLifecycleOwner
        ) {
            viewModel.handleEvent(it)
        }
    }

    private fun Spinner.selectQuery(query: String?) {
        if (this.count == 0 ||
            query.isNullOrBlank()
        ) return

        for (i in 0 until this.count) {
            if (this.getItemAtPosition(i) == query) {
                this.setSelection(i)
                break
            }
        }
    }

    private fun Spinner.bindCityList(list: List<City>, selectAction: ((Int) -> Unit)? = null) {
        if (list.isEmpty()) return

        val cityNames = mutableListOf<String>()

        for (i in list.indices) cityNames.add(list[i].name)

        val aa: ArrayAdapter<String> =
            ArrayAdapter(context, android.R.layout.simple_spinner_item, cityNames)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = aa
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                selectAction?.invoke(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        this.visible(true)
    }

    private fun setupSearchViewListener(list: List<Client>) {
        binding?.itemSearch?.mtSearchView?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filteredList = mutableListOf<Client>()

                    for (client: Client in list)
                        if (client.name.isMatch(it)) filteredList.add(client)
                        else if (client.phone.isMatch(it)) filteredList.add(client)
                        else if (client.locationEntry.cityName?.isMatch(it) == true)
                            filteredList.add(client)
                    if (filteredList.isNotEmpty()) clientAdapter.submitList(filteredList)

                }
                return true
            }
        })
    }

    private fun bindClient(client: Client) {
        binding?.apply {
            edTitle.text = client.name.toEditable()
            itemShareClient.apply {
                edPhone.text = client.phone.toEditable()
                itemShareClient.citySpinner.selectQuery(query = client.locationEntry.cityName)
            }
        }
    }

    private fun ClientViewModel.setupStatesObserver() {
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
        cityList.observe(viewLifecycleOwner) {
            it?.let { list ->
                binding?.itemShareClient?.citySpinner?.bindCityList(
                    list = list,
                    selectAction = { position ->
                        handleEvent(ClientEvent.OnSpinnerCitySelect(position))
                    })
            }
        }
        client.observe(viewLifecycleOwner) {
            bindClient(it)
        }
        clientList.observe(viewLifecycleOwner) {
            clientAdapter.submitList(it)
            setupSearchViewListener(it)
        }
        dialClientAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.actionDial(it)
        })
        messageClientAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.actionMsgWhatsApp(it)
        })
        filterClientAttempt.observe(viewLifecycleOwner, EventObserver {
            if (findNavController().currentDestination?.id == R.id.clientView) findNavController().navigate(
                ClientViewDirections.navigateToFilterList(
                    searchQuery = it
                )
            )
        })
        addCityNavigateAttempt.observe(viewLifecycleOwner, EventObserver {
            viewInteract.displaySnack(
                msg = getString(R.string.empty_city_list),
                aMsg = getString(R.string.add_c),
                action = {
                    if (findNavController().currentDestination?.id == R.id.clientView) findNavController().navigate(
                        ClientViewDirections.navigateToCity()
                    )
                }
            )
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.txt_update) viewModel.handleEvent(
            ClientEvent.OnUpdateTxtClick(
                name = binding?.edTitle?.text?.trim().toString(),
                phone = binding?.itemShareClient?.edPhone?.text?.trim().toString()
            )
        )
    }
}