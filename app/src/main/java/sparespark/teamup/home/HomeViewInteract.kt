package sparespark.teamup.home

import com.google.android.material.snackbar.Snackbar

interface HomeViewInteract {
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
    fun actionDial(phoneNum: String)
    fun actionMsgWhatsApp(phoneNum: String)
    fun actionCopyText(text: String)
    fun actionShareText(text: String)
    fun actionDataExport(intentAction: String)
    fun updateProgressLoad(loading: Boolean)
    fun updateActionBarTitle(title: String?)
    fun updateActionBarTitleColor(isError: Boolean)
}