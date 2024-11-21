package sparespark.teamup.notedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sparespark.teamup.R
import sparespark.teamup.core.MAX_INPUT_NOTE_DIG
import sparespark.teamup.core.MIN_INPUT_NAME_DIG
import sparespark.teamup.core.beginLayoutTitleLengthWatcher
import sparespark.teamup.core.binding.ViewBindingHolder
import sparespark.teamup.core.binding.ViewBindingHolderImpl
import sparespark.teamup.core.isNewStringItem
import sparespark.teamup.core.toEditable
import sparespark.teamup.core.visible
import sparespark.teamup.core.wrapper.EventObserver
import sparespark.teamup.databinding.NotedetailsViewBinding
import sparespark.teamup.home.HomeActivityInteract
import sparespark.teamup.notedetails.buildlogic.NoteInjector

class NoteDetailsView : Fragment(),
    View.OnClickListener,
    ViewBindingHolder<NotedetailsViewBinding> by ViewBindingHolderImpl() {

    private lateinit var viewModel: NoteViewModel
    private lateinit var viewInteract: HomeActivityInteract

    override fun onDestroyView() {
        super.onDestroyView()
        destroyBinding()
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.btn_action) viewModel.handleEvent(
            NoteEvent.OnUpdateTxtClick(
                title = binding?.itemTitle?.edText?.text?.trim().toString()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(NotedetailsViewBinding.inflate(layoutInflater), this@NoteDetailsView) {
        setupViewInteract()
        setupViewInputs()
        setupViewModel()
        viewModel.startObserving()
        setupClickListener()
    }

    private fun setupViewInteract() {
        viewInteract = activity as HomeActivityInteract
    }

    private fun setupViewInputs() {
        binding?.apply {
            itemTitle.apply {
                textInputLayout.hint = getString(R.string.note)
                edText.beginLayoutTitleLengthWatcher(
                    inputLayout = itemTitle.textInputLayout,
                    minDig = MIN_INPUT_NAME_DIG,
                    maxDig = MAX_INPUT_NOTE_DIG,
                    eMsg = getString(R.string.invalid)
                )
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            owner = this@NoteDetailsView,
            factory = NoteInjector(requireActivity().application).provideViewModelFactory()
        )[NoteViewModel::class.java]
        viewModel.handleEvent(NoteEvent.OnStartGetNote)
    }

    private fun setupClickListener() {
        binding?.apply {
            btnAction.setOnClickListener(this@NoteDetailsView)
            switchAdmin.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView?.isPressed == true) viewModel.handleEvent(
                    NoteEvent.OnAdminSwitchCheck(
                        admin = isChecked
                    )
                )
            }
        }
    }

    private fun NoteViewModel.startObserving() {
        loading.observe(viewLifecycleOwner) {
            binding?.progressBar?.visible(it)
        }
        error.observe(viewLifecycleOwner) {
            viewInteract.displayToast(it.asString(context))
        }
        updated.observe(viewLifecycleOwner, EventObserver {
            if (findNavController().currentDestination?.id == R.id.noteDetailsView) findNavController().navigate(
                NoteDetailsViewDirections.navigateToTransactionList()
            )
        })
        adminSwitchState.observe(viewLifecycleOwner) {
            binding?.switchAdmin?.isChecked = it
        }
        noteTxtValidateState.observe(viewLifecycleOwner) {
            binding?.itemTitle?.textInputLayout?.error = getString(R.string.invalid)
        }
        note.observe(viewLifecycleOwner) {
            if (!it.id.isNewStringItem()) binding?.itemTitle?.edText?.text = it.title.toEditable()
        }
    }
}