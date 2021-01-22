package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse
import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.http.HttpRequest
import no.nav.security.token.support.core.jwt.JwtTokenClaims
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler


private val issuer_isso = "isso-idtoken"

fun lagSikkerhetsfilter(javalin: Javalin, issuerProperties: IssuerProperties, tillateUrl: List<String>) {
    javalin.before { context ->
        val url: String = context.req.requestURL.toString()

        val erÅpenUrl = tillateUrl.contains(url)

        if (!erÅpenUrl) {
            try {
                val tokenValidationHandler =
                    JwtTokenValidationHandler(MultiIssuerConfiguration(mapOf(Pair(issuer_isso, issuerProperties))))
                val tokenValidationContext = tokenValidationHandler.getValidatedTokens(getHttpRequest(context))
                val claims = tokenValidationContext.getClaims(issuer_isso)
                opprettInnloggetVeileder(claims)
            } catch (e: Exception) {
                throw ForbiddenResponse()
            }
        }
    }
}

private fun opprettInnloggetVeileder(claims: JwtTokenClaims) =
    InnloggetVeileder(
        userName = claims["unique_name"].toString(),
        displayName = claims["name"].toString(),
        navIdent = claims["NAVident"].toString()
    ).apply { validate() }


private fun getHttpRequest(context: Context): HttpRequest = object : HttpRequest {
    override fun getHeader(headerName: String?) = context.headerMap()[headerName]
    override fun getCookies() = context.cookieMap().map { (name, value) ->
        object : HttpRequest.NameValue {
            override fun getName() = name
            override fun getValue() = value
        }
    }.toTypedArray()
}

data class InnloggetVeileder(
    val userName: String,
    val displayName: String,
    val navIdent: String
) {

    fun validate() {
        if (listOf<String>(userName, displayName, navIdent).any { s -> s.isEmpty() }) {
            throw RuntimeException("Ugyldig token")
        }
    }

}
