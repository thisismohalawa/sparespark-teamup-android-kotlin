package sparespark.teamup.transaction.itemlist

sealed class TransactionListEvent {
    data object OnViewStart : TransactionListEvent()
}
