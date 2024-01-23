package sparespark.teamup.items.itemdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sparespark.teamup.common.binding.ViewBindingHolder
import sparespark.teamup.common.binding.ViewBindingHolderImpl
import sparespark.teamup.databinding.ItemdetailsViewBinding

class ItemDetailsView() : Fragment(),
    View.OnClickListener,
    ViewBindingHolder<ItemdetailsViewBinding> by ViewBindingHolderImpl() {

    override fun onClick(view: View?) {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(ItemdetailsViewBinding.inflate(layoutInflater), this@ItemDetailsView) {

        setUpViewClickListener()
    }

    private fun setUpViewClickListener() {

    }
}
