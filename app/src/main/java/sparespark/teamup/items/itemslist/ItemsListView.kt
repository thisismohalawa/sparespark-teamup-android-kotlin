package sparespark.teamup.items.itemslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sparespark.teamup.common.binding.ViewBindingHolder
import sparespark.teamup.common.binding.ViewBindingHolderImpl
import sparespark.teamup.databinding.ItemslistViewBinding

class ItemsListView() : Fragment(),
    View.OnClickListener,
    ViewBindingHolder<ItemslistViewBinding> by ViewBindingHolderImpl() {

    override fun onClick(view: View?) {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = initBinding(ItemslistViewBinding.inflate(layoutInflater), this@ItemsListView) {

        setUpViewClickListener()
    }

    private fun setUpViewClickListener() {

    }
}
