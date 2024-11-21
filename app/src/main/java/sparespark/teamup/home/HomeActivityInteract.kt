package sparespark.teamup.home

import com.google.android.material.snackbar.Snackbar

interface HomeActivityInteract {
    fun displayToast(msg: String?)
    fun displaySnack(
        msg: String,
        duration: Int = Snackbar.LENGTH_LONG,
        aMsg: String? = null,
        action: (() -> Unit)? = null
    )
    fun startAuthActivity()
    fun restartHomeActivity()
    fun finishHomeActivity()
    fun updateProgressLoad(loading: Boolean)
    fun updateActionBarTitle(title: String?)
    fun updateActionBarTitleColor(isError: Boolean)
    fun actionDial(phoneNum: String)
    fun actionCopyText(text: String)
    fun actionShareText(text: String)
}