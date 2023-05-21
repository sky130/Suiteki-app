package ml.sky233.suiteki.util

import java.net.URI
import java.net.URLDecoder

object UrlUtils {
    fun getParameter(url: String, keyWord: String): String? {
        var retValue: String? = null
        try {
            val charset = "utf-8"
            val uri = URI(url)
            val query = uri.query
            if (query != null) {
                val keyValues = query.split("&")
                for (keyValue in keyValues) {
                    val keyValuePair = keyValue.split("=")
                    if (keyValuePair.size == 2) {
                        val key = URLDecoder.decode(keyValuePair[0], charset)
                        val value = URLDecoder.decode(keyValuePair[1], charset)
                        if (key == keyWord) {
                            if (!value.isBlank()) {
                                retValue = value
                            }
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return retValue

    }
}