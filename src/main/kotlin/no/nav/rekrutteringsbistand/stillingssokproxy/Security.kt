package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse
import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.http.HttpRequest
import no.nav.security.token.support.core.jwt.JwtTokenClaims
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler

fun lagSikkerhetsfilter(javalin: Javalin, issuerProperties: IssuerProperties, tillateUrl: List<String>) {
    javalin.before { context ->
        val url: String = context.req.requestURL.toString()
        val endepunktTillattUtenAutentisering = tillateUrl.any { url.contains(it) }

        if (!endepunktTillattUtenAutentisering) {
            val cookieName = issuerProperties.cookieName
            val tokenValidationHandler =
                JwtTokenValidationHandler(MultiIssuerConfiguration(mapOf(Pair(cookieName, issuerProperties))))
            val tokenValidationContext = tokenValidationHandler.getValidatedTokens(getHttpRequest(context))
            val claims = tokenValidationContext.getClaims(cookieName)

            if (!tokenErGyldig(claims)) {
                throw ForbiddenResponse()
            }
        }
    }
}

fun tokenErGyldig(claims: JwtTokenClaims?): Boolean {
    log("tokenErGyldig()").info("Claims: ${claims?.allClaims?.keys?.joinToString()}")
    if (claims == null || claims["NAVident"] == null) return false
    return claims["NAVident"].toString().isNotEmpty()
}

private fun getHttpRequest(context: Context): HttpRequest = object : HttpRequest {
    override fun getHeader(headerName: String?) = context.headerMap()[headerName]
    override fun getCookies() = context.cookieMap().map { (name, value) ->
        object : HttpRequest.NameValue {
            override fun getName() = name
            override fun getValue() = value
        }
    }.toTypedArray()
}
