package rip.deadcode.xkcdtool.server

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import rip.deadcode.xkcdtool.core.askUrl
import rip.deadcode.xkcdtool.core.regularize


class Handler : AbstractHandler() {

    override fun handle(
        target: String,
        baseRequest: Request,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {

        val isExplain = baseRequest.getParameter("explain") != null || baseRequest.getParameter("e") != null
        // Drop `/` of the root. Other slashes are preserved as are
        val nameQuery = regularize(baseRequest.requestURI.drop(1))

        val redirectUrl = askUrl(nameQuery, isExplain)
        if (redirectUrl.isPresent) {
            response.status = 307
            response.addHeader("Location", redirectUrl.get())

        } else {
            response.status = 404
        }

        baseRequest.isHandled = true
    }
}
