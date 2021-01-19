package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import io.javalin.http.Context
import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.http.HttpRequest
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler
import no.nav.security.token.support.filter.JwtTokenValidationFilter
import java.net.URL
import javax.servlet.http.Cookie

class Security {

    private val ISSUER_ISSO = "isso-idtoken"

    fun lagSikkerhetsfilter(javalin: Javalin, tillateUrl: List<String>) {
        javalin.before { context ->
            val url = context.req.requestURL
            val erÅpenUrl = tillateUrl.any { tillattUrl -> url.contains(tillattUrl) }

            if (!erÅpenUrl) {
                // Legge til try/catch?
                val tokenValidationHandler = JwtTokenValidationHandler(getMultiIssuerConfiguration())
                log("Sikkerhetsfilter").info("CookieMap: ${context.cookieMap()}")
                val tokenValidationContext = tokenValidationHandler.getValidatedTokens(getHttpRequest(context))

                log("Sikkerhetsfilter").info("TokenValidationContext: $tokenValidationContext")


                val tokenValidationFilter = JwtTokenValidationFilter(
                    tokenValidationHandler,
                    TokenValidationContextHolderImpl(tokenValidationContext)
                )
                tokenValidationFilter.doFilter(context.req, context.res) { request, response -> }

                val issuers = tokenValidationContext.issuers
                log("Sikkerhetsfilter").info("Issuers: $issuers")

                val jwt = tokenValidationContext.getJwtToken(ISSUER_ISSO)
                log("Sikkerhetsfilter").info("JWT: $jwt")

                val claims = tokenValidationContext.getClaims(ISSUER_ISSO)
                log("Sikkerhetsfilter").info("Claims: $claims")
            }
        }
    }

    private fun getMultiIssuerConfiguration(): MultiIssuerConfiguration {
        val properties = IssuerProperties()
        properties.cookieName = ISSUER_ISSO
        properties.discoveryUrl =
            URL("https://login.microsoftonline.com/NAVQ.onmicrosoft.com/.well-known/openid-configuration")
        properties.acceptedAudience = listOf("38e07d31-659d-4595-939a-f18dce3446c5", "prod-fss:arbeidsgiver:rekrutteringsbistand-stilling")
        return MultiIssuerConfiguration(mapOf(Pair(ISSUER_ISSO, properties)))
    }

    private fun getHttpRequest(context: Context): HttpRequest = object : HttpRequest {
        override fun getHeader(headerName: String?) = context.headerMap()[headerName]
        override fun getCookies() = context.cookieMap().map { (name, value) -> NameValueImpl(Cookie(name, value)) }.toTypedArray()
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
