package sparespark.teamup.city

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
import sparespark.teamup.databinding.ItemHeaderBinding

class CityAdapter(
    val event: MutableLiveData<CityEvent> = MutableLiveData()
) : ListAdapter<City, RecyclerView.ViewHolder>(CityDiffUtilCallback()) {

    inner class CityViewHolder(var binding: ItemHeaderBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CityViewHolder(
            ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val city = getItem(position)
        with(holder as CityViewHolder) {
            with(binding) {
                txtTitle.text = city.name
                txtSubtitle.text = itemView.context.getString(R.string.city_name)
                imgAction.setCustomImage(R.drawable.ic_menu, itemView.context)
                imgAction.setOnClickListener {
                    it.inflateMenuList(position)
                }
            }
            itemView.setOnClickListener {
                event.value = CityEvent.OnListItemClick(position)
            }
        }
    }

    private fun View.inflateMenuList(position: Int) {
        val popupMenu = PopupMenu(this@inflateMenuList.context, this)
        popupMenu.apply {
            inflate(R.menu.city_menu)
            menu.findItem(R.id.delete_menu).setRedTitle()
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.refresh_menu -> event.value = CityEvent.OnMenuRefreshClick

                    R.id.expand_menu -> event.value = CityEvent.OnStartGetCity

                    R.id.city_items_menu -> event.value =
                        CityEvent.OnMenuNavigateCityClick(position)

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
