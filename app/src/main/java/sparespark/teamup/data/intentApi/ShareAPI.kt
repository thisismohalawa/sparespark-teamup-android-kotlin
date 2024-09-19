package sparespark.teamup.data.intentApi

import sparespark.teamup.core.wrapper.Result

interface ShareAPI {
    fun shareText(text: String): Result<Exception, Unit>
    fun copyText(text: String): Result<Exception, Unit>
    fun dial(phoneNum: String): Result<Exception, Unit>
    fun messageVieWhatsApp(phoneNum: String): Result<Exception, Unit>
}