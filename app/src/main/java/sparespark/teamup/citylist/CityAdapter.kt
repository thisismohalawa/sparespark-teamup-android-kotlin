package sparespark.teamup.citylist

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
import sparespark.teamup.data.model.city.City
import sparespark.teamup.databinding.ItemSimpleHeaderBinding

class CityAdapter(
    val event: MutableLiveData<CityEvent> = MutableLiveData()
) : ListAdapter<City, CityAdapter.CityViewHolder>(CityDiffUtilCallback()) {

    inner class CityViewHolder(var binding: ItemSimpleHeaderBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder =
        CityViewHolder(
            ItemSimpleHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = getItem(position)
        val iContext = holder.itemView.context
        with(holder.binding) {
            txtTitle.text = city.name
            txtSubtitle.text = iContext.getString(R.string.city_name)
            imgAction.setCustomImage(R.drawable.ic_menu, iContext)
            imgAction.setOnClickListener {
                it.inflateMenuList(position)
            }
            root.setOnClickListener {
                event.value = CityEvent.OnListItemClick(position)
            }
            root.setOnLongClickListener {
                event.value = CityEvent.OnListItemLongClick(holder.adapterPosition)
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
                    R.id.refresh_menu -> event.value = CityEvent.OnMenuRefreshClick

                    R.id.expand_menu -> event.value = CityEvent.OnStartGetCity


                    R.id.delete_menu -> context.displayConfirmDialog(
                        title = R.string.delete_permanently
                    ) {
                        event.value = CityEvent.OnMenuDeleteClick(position)
                    }
                }
                false
            }
            show()
        }
    }
}
