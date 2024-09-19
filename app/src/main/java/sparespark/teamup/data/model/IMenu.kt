package sparespark.teamup.data.model

data class IMenu(
    val id: Int,
    val title: Int,
    val isNav: Boolean = true,
    val isRedColored: Boolean = false,
    val des: Int? = null
)
