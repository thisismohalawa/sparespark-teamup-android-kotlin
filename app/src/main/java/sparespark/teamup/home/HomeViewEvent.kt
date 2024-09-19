package sparespark.teamup.home

sealed class HomeViewEvent {
    data object OnStartGetUser : HomeViewEvent()
}