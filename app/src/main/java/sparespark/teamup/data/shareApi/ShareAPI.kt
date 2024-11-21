package sparespark.teamup.data.shareApi

import sparespark.teamup.core.wrapper.Result

interface ShareAPI {
    fun shareText(text: String): Result<Exception, Unit>
    fun copyText(text: String): Result<Exception, Unit>
    fun dial(phoneNum: String): Result<Exception, Unit>
}