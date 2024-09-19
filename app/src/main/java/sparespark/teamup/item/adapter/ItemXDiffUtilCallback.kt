package sparespark.teamup.item.adapter

import androidx.recyclerview.widget.DiffUtil
import sparespark.teamup.data.model.item.Item

class ItemXDiffUtilCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }
}
