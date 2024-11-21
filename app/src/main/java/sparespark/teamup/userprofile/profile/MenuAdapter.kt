package sparespark.teamup.userprofile.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sparespark.teamup.R
import sparespark.teamup.core.setCustomColor
import sparespark.teamup.core.setCustomImage
import sparespark.teamup.core.visible
import sparespark.teamup.data.model.ProfileMenu
import sparespark.teamup.databinding.ItemSimpleHeaderBinding
import sparespark.teamup.userprofile.UserEvent

class MenuAdapter(
    val list: List<ProfileMenu>,
    val event: MutableLiveData<UserEvent> = MutableLiveData()
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(var binding: ItemSimpleHeaderBinding) : ViewHolder(
        binding.root
    )

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder =
        MenuViewHolder(
            ItemSimpleHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = list[position]
        val iContext = holder.itemView.context
        with(holder.binding) {
            imgAction.setCustomImage(R.drawable.ic_arrow_nav, iContext)

            txtTitle.text = iContext.getString(menu.title)
            txtSubtitle.text = menu.des?.let { iContext.getString(it) }

            imgAction.visible(isVisible = menu.isNav)
            txtSubtitle.visible(isVisible = menu.des != null)

            if (menu.isRedColored) txtTitle.setCustomColor(R.color.red, iContext)

            holder.itemView.setOnClickListener {
                event.value = UserEvent.OnMenuItemClick(menu.id)
            }
        }
    }
}

