package sparespark.teamup.teamlist

import androidx.recyclerview.widget.DiffUtil
import sparespark.teamup.data.model.team.Team

class TeamDiffUtilCallback : DiffUtil.ItemCallback<Team>() {
    override fun areItemsTheSame(oldItem: Team, newItem: Team): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: Team, newItem: Team): Boolean {
        return oldItem.uid == newItem.uid
    }
}
