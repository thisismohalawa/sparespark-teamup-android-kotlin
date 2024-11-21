package sparespark.teamup.companylist

import androidx.recyclerview.widget.DiffUtil
import sparespark.teamup.data.model.company.Company

class CompanyDiffUtilCallback : DiffUtil.ItemCallback<Company>() {
    override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean {
        return oldItem.id == newItem.id
    }
}
