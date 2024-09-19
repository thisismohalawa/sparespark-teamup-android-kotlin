package sparespark.teamup.city

import androidx.recyclerview.widget.DiffUtil
import sparespark.teamup.data.model.city.City

class CityDiffUtilCallback : DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem.id == newItem.id
    }
}
