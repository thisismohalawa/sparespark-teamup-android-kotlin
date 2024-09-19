package sparespark.teamup.team


sealed class TeamEvent {
    data object GetTeam : TeamEvent()
    data class OnItemListSwitchCheck(val pos: Int, val active: Boolean) : TeamEvent()
}
