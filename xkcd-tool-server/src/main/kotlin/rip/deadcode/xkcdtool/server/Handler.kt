package rip.deadcode.xkcdtool.server

import com.google.common.net.MediaType
import jakarta.servlet.ServletRequest
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import rip.deadcode.xkcdtool.core.OutputType
import rip.deadcode.xkcdtool.core.OutputType.EXPLAIN
import rip.deadcode.xkcdtool.core.OutputType.IMAGE
import rip.deadcode.xkcdtool.core.OutputType.JSON
import rip.deadcode.xkcdtool.core.OutputType.NORMAL
import rip.deadcode.xkcdtool.core.askUrl
import rip.deadcode.xkcdtool.core.regularize
import java.util.*


class Handler : AbstractHandler() {

    override fun handle(
        target: String,
        baseRequest: Request,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {

        // Drop `/` of the root. Other slashes are preserved as are
        val nameQuery = regularize(baseRequest.requestURI.drop(1))

        if (nameQuery.first().isEmpty()) {
            response.status = 307
            response.addHeader("Location", "https://github.com/minebreaker/xkcd-tool")
            baseRequest.isHandled = true
            return
        }

        val mode = guessOutputType(baseRequest).orElse(IMAGE)

        val redirectUrl = askUrl(nameQuery, mode)
        if (redirectUrl.isPresent) {
            response.status = 307
            response.addHeader("Location", redirectUrl.get())

        } else {
            response.status = 404
        }

        baseRequest.isHandled = true
    }
}

fun guessOutputType(request: Request): Optional<OutputType> {
    val isExplainFlagged = request.hasTruthyParam("explain") || request.hasTruthyParam("e")
    if (isExplainFlagged) {
        return Optional.of(EXPLAIN)
    }

    return getOutputTypeFromStr(request.getParameter("type"))
        .or { getOutputTypeByHeader(request.getHeader("Accept")) }
}

fun ServletRequest.hasTruthyParam(name: String): Boolean {
    val param = this.getParameter(name)?.toLowerCase()
    return param == "" || param == "true" || param == "yes" || param == "y" || param == "1"
}

fun getOutputTypeFromStr(str: String?): Optional<OutputType> {
    return Optional.ofNullable(
        when (str?.toLowerCase()) {
            "image" -> IMAGE
            "html" -> NORMAL
            "json" -> JSON
            "explain" -> EXPLAIN
            else -> null
        }
    )
}

@Suppress("UnstableApiUsage")
fun getOutputTypeByHeader(accept: String): Optional<OutputType> {
    val mime = try {
        MediaType.parse(accept)
    } catch (e: IllegalArgumentException) {
        return Optional.empty()
    }

    return Optional.ofNullable(
        when {
            mime.type() == "image" -> IMAGE
            mime.type() == "text" && mime.subtype() == "html" -> NORMAL
            mime.type() == "application" && mime.subtype() == "json" -> JSON
            else -> null
        }
    )
}
