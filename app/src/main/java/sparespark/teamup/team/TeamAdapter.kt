package sparespark.teamup.team

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sparespark.teamup.R
import sparespark.teamup.core.enable
import sparespark.teamup.core.toRoleTitle
import sparespark.teamup.data.model.team.Team
import sparespark.teamup.databinding.ItemTeamBinding

class TeamAdapter(
    val event: MutableLiveData<TeamEvent> = MutableLiveData()
) : ListAdapter<Team, RecyclerView.ViewHolder>(TeamDiffUtilCallback()) {

    inner class TeamViewHolder(var binding: ItemTeamBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TeamViewHolder(
            ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val team = getItem(position)
        with(holder as TeamViewHolder) {
            with(binding) {
                itemHeader.txtTitle.text = team.name
                itemHeader.txtHint.text = team.roleId.toRoleTitle(itemView.context)
                txtDes.text = if (team.lastLogin.isBlank()) team.email
                else team.email + "\n${itemView.context.getString(R.string.last_login)}: ${team.lastLogin}"
                team.activated.let {
                    switchAction.isChecked = it
                    itemHeader.txtTitle.enable(it)
                    itemHeader.txtHint.enable(it)
                    txtDes.enable(it)
                }
                switchAction.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (buttonView?.isPressed == true) event.value =
                        TeamEvent.OnItemListSwitchCheck(
                            active = isChecked, pos = position
                        )
                }
            }
        }
    }
}
