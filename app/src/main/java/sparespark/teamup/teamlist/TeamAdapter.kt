package sparespark.teamup.teamlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.core.enable
import sparespark.teamup.core.internal.toRoleTitle
import sparespark.teamup.data.model.team.Team
import sparespark.teamup.databinding.ItemTeamBinding

class TeamAdapter(
    val event: MutableLiveData<TeamEvent> = MutableLiveData()
) : ListAdapter<Team, TeamAdapter.TeamViewHolder>(TeamDiffUtilCallback()) {

    inner class TeamViewHolder(var binding: ItemTeamBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder =
        TeamViewHolder(
            ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val team = getItem(position)
        with(holder.binding) {
            itemHeader.txtTitle.text = team.name
            itemHeader.txtHint.text = "(${team.roleId.toRoleTitle(holder.itemView.context)})"
            txtDes.text = team.email

            team.activated.let {
                switchAction.isChecked = it
                itemHeader.txtTitle.enable(it)
                itemHeader.txtHint.enable(it)
                txtDes.enable(it)
            }

            switchAction.setOnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView?.isPressed == true) event.value = TeamEvent.OnItemListSwitchCheck(
                    active = isChecked, pos = position
                )
            }
        }
    }
}