package sparespark.teamup.productlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.R
import sparespark.teamup.core.displayConfirmDialog
import sparespark.teamup.core.setCustomImage
import sparespark.teamup.core.setRedTitle
import sparespark.teamup.data.model.product.Product
import sparespark.teamup.databinding.ItemSimpleHeaderBinding

class ProductAdapter(
    val event: MutableLiveData<ProductEvent> = MutableLiveData()
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffUtilCallback()) {

    inner class ProductViewHolder(var binding: ItemSimpleHeaderBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(
            ItemSimpleHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        val iContext = holder.itemView.context
        with(holder.binding) {
            txtTitle.text = product.name
            txtSubtitle.text =
                product.companyEntry.companyName?.ifBlank { iContext.getString(R.string.product) }
            imgAction.setCustomImage(R.drawable.ic_menu, iContext)
            imgAction.setOnClickListener {
                it.inflateMenuList(position)
            }
            root.setOnClickListener {
                event.value = ProductEvent.OnListItemClick(position)
            }

        }
    }

    private fun View.inflateMenuList(position: Int) {
        val popupMenu = PopupMenu(this@inflateMenuList.context, this)
        popupMenu.apply {
            inflate(R.menu.data_list_menu)
            menu.findItem(R.id.delete_menu).setRedTitle()
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.refresh_menu -> event.value = ProductEvent.OnMenuRefreshClick

                    R.id.expand_menu -> event.value = ProductEvent.OnStartGetProduct


                    R.id.delete_menu -> context.displayConfirmDialog(
                        title = R.string.delete_permanently
                    ) {
                        event.value = ProductEvent.OnMenuDeleteClick(position)
                    }
                }
                false
            }
            show()
        }
    }
}
