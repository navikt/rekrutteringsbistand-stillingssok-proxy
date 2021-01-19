package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.http.HttpRequest
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler
import no.nav.security.token.support.filter.JwtTokenValidationFilter
import java.net.URL
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

object Security {

    private val ISSUER_ISSO = "isso"

    fun lagSikkerhetsfilter(javalin: Javalin, tillateUrl: List<String>) {
        javalin.before { context ->
            val url = context.req.requestURL
            val erÅpenUrl = tillateUrl.any { tillattUrl -> url.contains(tillattUrl) }

            if (!erÅpenUrl) {
                val tokenValidationHandler = JwtTokenValidationHandler(getMultiIssuerConfiguration())
                val tokenValidationContext = tokenValidationHandler.getValidatedTokens(getHttpRequest(context.req))

                val tokenValidationFilter = JwtTokenValidationFilter(
                    tokenValidationHandler,
                    TokenValidationContextHolderImpl(tokenValidationContext)
                )
                tokenValidationFilter.doFilter(context.req, context.res) { request, response -> }

                val claims = tokenValidationContext.getClaims(ISSUER_ISSO).run {
                    InnloggetVeileder(
                        userName = get("unique_name").toString(),
                        displayName = get("name").toString(),
                        navIdent = get("NAVident").toString()
                    )
                }
                log("Sikkerhetsfilter").info("InnloggetVeileder: $claims")
            }
        }
    }

    private fun getMultiIssuerConfiguration(): MultiIssuerConfiguration {
        val properties = IssuerProperties()
        properties.cookieName = "isso"
        properties.discoveryUrl =
            URL("https://login.microsoftonline.com/NAVQ.onmicrosoft.com/.well-known/openid-configuration")
        properties.acceptedAudience = listOf("dev-fss:arbeidsgiver:rekrutteringsbistand-stilling", "prod-fss:arbeidsgiver:rekrutteringsbistand-stilling")
        return MultiIssuerConfiguration(mapOf(Pair("rekrutteringsbistand-stillingssok-proxy", properties)))
    }

    private fun getHttpRequest(servletRequest: HttpServletRequest): HttpRequest = object : HttpRequest {
        override fun getHeader(headerName: String?) = servletRequest.getHeader(headerName)
        override fun getCookies() = servletRequest.cookies.map { cookie -> NameValueImpl(cookie) }.toTypedArray()
    }

    private class NameValueImpl(val cookie: Cookie) : HttpRequest.NameValue {
        override fun getName() = cookie.name
        override fun getValue() = cookie.value
    }

    private class TokenValidationContextHolderImpl(private var tokenValidationContext: TokenValidationContext?) :
        TokenValidationContextHolder {
        override fun getTokenValidationContext() = tokenValidationContext

        override fun setTokenValidationContext(tokenValidationContext: TokenValidationContext?) {
            this.tokenValidationContext = tokenValidationContext
        }
    }

    data class InnloggetVeileder(
        val userName: String,
        val displayName: String,
        val navIdent: String
    )
}
