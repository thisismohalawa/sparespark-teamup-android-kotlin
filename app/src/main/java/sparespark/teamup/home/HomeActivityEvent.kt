package sparespark.teamup.home

sealed class HomeActivityEvent {
    data object OnStartGetUser : HomeActivityEvent()
}