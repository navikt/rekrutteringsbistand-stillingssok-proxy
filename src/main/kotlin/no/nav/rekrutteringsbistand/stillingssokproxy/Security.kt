package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.http.HttpRequest
import no.nav.security.token.support.core.validation.JwtTokenRetriever
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

object Security {

    fun lagSikkerhetsfilter(javalin: Javalin, tillateUrl: List<String>) {
        javalin.before { context ->
            val url = context.req.requestURL
            val erTillattUrl = tillateUrl.any { tillattUrl -> url.contains(tillattUrl) }

            if (!erTillattUrl) {
                val httpRequest = getHttpRequest(context.req)
                // Hvor kan hentes fra
                val ikkeValiderteTokens =
                    JwtTokenRetriever.retrieveUnvalidatedTokens(getMultiIssuerConfiguration(), httpRequest)

            }

        }
    }

    private fun getMultiIssuerConfiguration(): MultiIssuerConfiguration {
        val properties = IssuerProperties()
        // Set opp properties
        return MultiIssuerConfiguration(mapOf(Pair("Vår App", properties)))
    }

    private fun getHttpRequest(servletRequest: HttpServletRequest): HttpRequest = object : HttpRequest {
        override fun getHeader(headerName: String?) = servletRequest.getHeader(headerName)
        override fun getCookies() = servletRequest.cookies.map { cookie -> NameValueImpl(cookie) }.toTypedArray()
    }

    private class NameValueImpl(val cookie: Cookie) : HttpRequest.NameValue {
        override fun getName() = cookie.name
        override fun getValue() = cookie.value
    }
}
