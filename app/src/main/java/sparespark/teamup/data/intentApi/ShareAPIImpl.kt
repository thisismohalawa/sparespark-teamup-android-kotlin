package sparespark.teamup.data.intentApi

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import sparespark.teamup.core.wrapper.Result

class ShareAPIImpl(
    val context: Context
) : ShareAPI {

    override fun shareText(text: String): Result<Exception, Unit> = Result.build {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "TeamupApp")
        intent.putExtra(Intent.EXTRA_TEXT, text)
        context.startActivity(Intent.createChooser(intent, "Share Text."))
    }

    override fun copyText(text: String): Result<Exception, Unit> = Result.build {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }

    override fun dial(phoneNum: String): Result<Exception, Unit> = Result.build {
        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNum"))
        context.startActivity(callIntent)
    }

    override fun messageVieWhatsApp(phoneNum: String): Result<Exception, Unit> = Result.build {
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse(
                String.format(
                    "https://api.whatsapp.com/send?phone=%s&text=%s", phoneNum, ""
                )
            )
        )
        context.startActivity(intent)
    }

}