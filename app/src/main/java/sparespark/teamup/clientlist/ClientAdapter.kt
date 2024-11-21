package sparespark.teamup.clientlist

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
import sparespark.teamup.data.model.client.Client
import sparespark.teamup.databinding.ItemSimpleHeaderBinding

class ClientAdapter(
    val event: MutableLiveData<ClientEvent> = MutableLiveData()
) : ListAdapter<Client, ClientAdapter.ClientViewHolder>(ClientDiffUtilCallback()) {

    inner class ClientViewHolder(var binding: ItemSimpleHeaderBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder =
        ClientViewHolder(
            ItemSimpleHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = getItem(position)
        val iContext = holder.itemView.context

        with(holder.binding) {
            txtTitle.text = client.name
            txtSubtitle.text =
                client.locationEntry.cityName?.ifBlank { iContext.getString(R.string.client) }
            imgAction.setCustomImage(R.drawable.ic_menu, iContext)
            imgAction.setOnClickListener {
                it.inflateItemMenu(position)
            }
            root.setOnClickListener {
                event.value = ClientEvent.OnListItemClick(position)
            }
            root.setOnLongClickListener {
                event.value = ClientEvent.OnListItemLongClick(holder.adapterPosition)
                true
            }
        }
    }

    private fun View.inflateItemMenu(position: Int) {
        val popupMenu = PopupMenu(this@inflateItemMenu.context, this)
        popupMenu.apply {
            inflate(R.menu.client_list_menu)
            menu.findItem(R.id.delete_menu).setRedTitle()
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.expand_menu -> event.value = ClientEvent.OnStartGetClient

                    R.id.refresh_menu -> event.value = ClientEvent.OnMenuRefreshClick


                    R.id.dial_menu -> event.value = ClientEvent.OnMenuDialClick(
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
