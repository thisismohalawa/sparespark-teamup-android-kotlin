package sparespark.teamup.client

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
import sparespark.teamup.data.model.client.Client
import sparespark.teamup.databinding.ItemHeaderBinding

class ClientAdapter(
    val event: MutableLiveData<ClientEvent> = MutableLiveData()
) : ListAdapter<Client, RecyclerView.ViewHolder>(ClientDiffUtilCallback()) {

    inner class ClientViewHolder(var binding: ItemHeaderBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ClientViewHolder(
            ItemHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val client = getItem(position)
        with(holder as ClientViewHolder) {
            with(binding) {
                txtTitle.text = client.name
                txtSubtitle.text =
                    if (client.locationEntry.cityName?.isNotBlank() == true) client.locationEntry.cityName else itemView.context.getString(
                        R.string.empty
                    )
                imgAction.setCustomImage(R.drawable.ic_menu, itemView.context)
                imgAction.setOnClickListener {
                    it.inflateItemMenu(position)
                }
            }
            itemView.setOnClickListener {
                event.value = ClientEvent.OnListItemClick(position)
            }
        }
    }

    private fun View.inflateItemMenu(position: Int) {
        val popupMenu = PopupMenu(this@inflateItemMenu.context, this)
        popupMenu.apply {
            inflate(R.menu.client_menu)
            menu.findItem(R.id.delete_menu).setRedTitle()
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.expand_menu -> event.value = ClientEvent.OnStartGetClient

                    R.id.refresh_menu -> event.value = ClientEvent.OnMenuRefreshClick

                    R.id.client_items_menu -> event.value =
                        ClientEvent.OnMenuNavigateClientClick(position)

                    R.id.dial_menu -> event.value = ClientEvent.OnMenuDialClick(
                        pos = position
                    )

                    R.id.msg_menu -> event.value = ClientEvent.OnMenuMsgClick(
                        pos = position
                    )

                    R.id.delete_menu -> context.displayConfirmDialog(
                        title = R.string.delete_permanently
                    ) {
                        event.value = ClientEvent.OnMenuDeleteClick(
                            pos = position
                        )
                    }

                }
                false
            }
            show()
        }
    }
}
