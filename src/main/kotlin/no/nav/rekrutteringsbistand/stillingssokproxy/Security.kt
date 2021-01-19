package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import io.javalin.http.Context
import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.http.HttpRequest
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler
import java.net.URL
import javax.servlet.http.Cookie

class Security {

    private val ISSUER_ISSO = "isso-idtoken"

    fun lagSikkerhetsfilter(javalin: Javalin, tillateUrl: List<String>) {
        javalin.before { context ->
            val url = context.req.requestURL
            val erÅpenUrl = tillateUrl.any { tillattUrl -> url.contains(tillattUrl) }
            log("Security").info("sjekkurl:${url}")

            if (!erÅpenUrl) {
                val tokenValidationHandler = JwtTokenValidationHandler(getMultiIssuerConfiguration())
                val tokenValidationContext = tokenValidationHandler.getValidatedTokens(getHttpRequest(context))

                val claims = tokenValidationContext.getClaims(ISSUER_ISSO)

                val innloggetVeileder = InnloggetVeileder(
                    claims["unique_name"].toString(),
                    claims["name"].toString(),
                    claims["NAVident"].toString()
                )

                log("Sikkerhetsfilter").info("InnloggetVeileder: $innloggetVeileder")
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

    data class InnloggetVeileder(
        val userName: String,
        val displayName: String,
        val navIdent: String
    )
}
