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
import sparespark.teamup.data.model.IMenu
import sparespark.teamup.databinding.ItemHeaderBinding
import sparespark.teamup.userprofile.UserEvent

class MenuAdapter(
    val list: List<IMenu>,
    val event: MutableLiveData<UserEvent> = MutableLiveData()
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(var binding: ItemHeaderBinding) : ViewHolder(
        binding.root
    )

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder =
        MenuViewHolder(
            ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val iMenu = list[position]
        with(holder) {
            with(binding) {
                imgAction.visible(isVisible = iMenu.isNav)
                itemView.context.let {
                    imgAction.setCustomImage(R.drawable.ic_arrow_nav, it)

                    txtTitle.text = it.getString(iMenu.title)
                    if (iMenu.isRedColored) txtTitle.setCustomColor(R.color.red, it)

                    if (iMenu.des != null) txtSubtitle.text = it.getString(iMenu.des)
                    else txtSubtitle.visible(false)
                }
            }
            itemView.setOnClickListener {
                event.value = UserEvent.OnMenuItemClick(iMenu.id)
            }
        }
    }
}

