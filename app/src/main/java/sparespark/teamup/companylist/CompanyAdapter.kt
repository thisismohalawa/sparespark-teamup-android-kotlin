package sparespark.teamup.companylist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.R
import sparespark.teamup.citylist.CityEvent
import sparespark.teamup.core.displayConfirmDialog
import sparespark.teamup.core.setCustomImage
import sparespark.teamup.core.setRedTitle
import sparespark.teamup.data.model.company.Company
import sparespark.teamup.databinding.ItemSimpleHeaderBinding

class CompanyAdapter(
    val event: MutableLiveData<CompanyEvent> = MutableLiveData()
) : ListAdapter<Company, CompanyAdapter.CompanyViewHolder>(CompanyDiffUtilCallback()) {

    inner class CompanyViewHolder(var binding: ItemSimpleHeaderBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder =
        CompanyViewHolder(
            ItemSimpleHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company = getItem(position)
        val iContext = holder.itemView.context
        with(holder.binding) {
            txtTitle.text = company.name
            txtSubtitle.text = iContext.getString(R.string.company)
            imgAction.setCustomImage(R.drawable.ic_menu, iContext)
            imgAction.setOnClickListener {
                it.inflateMenuList(position)
            }
            root.setOnClickListener {
                event.value = CompanyEvent.OnListItemClick(position)
            }
            root.setOnLongClickListener {
                event.value = CompanyEvent.OnListItemLongClick(holder.adapterPosition)
                true
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
                    R.id.refresh_menu -> event.value = CompanyEvent.OnMenuRefreshClick

                    R.id.expand_menu -> event.value = CompanyEvent.OnStartGetCompany


                    R.id.delete_menu -> context.displayConfirmDialog(
                        title = R.string.delete_permanently
                    ) {
                        event.value = CompanyEvent.OnMenuDeleteClick(position)
                    }
                }
                false
            }
            show()
        }
    }
}
