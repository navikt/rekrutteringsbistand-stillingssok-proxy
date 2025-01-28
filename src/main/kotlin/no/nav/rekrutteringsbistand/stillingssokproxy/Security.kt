package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse
import io.javalin.security.RouteRole
import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.http.HttpRequest
import no.nav.security.token.support.core.jwt.JwtTokenClaims

enum class Rolle : RouteRole {
    UNPROTECTED,
    VEILEDER_ELLER_SYSTEMBRUKER,
}

fun Context.sjekkTilgang(
    rolle: Rolle,
    issuerProperties: Map<Rolle, Pair<String, IssuerProperties>>
) {
    val claims = hentValidTokenClaims(this, issuerProperties, rolle)
    when (rolle) {
        Rolle.UNPROTECTED -> return
        Rolle.VEILEDER_ELLER_SYSTEMBRUKER ->
            if (autentiserVeilederEllerSystembruker(claims)) return
            else throw ForbiddenResponse("Ingen tilgang")
    }
}

fun autentiserVeilederEllerSystembruker(validClaims: JwtTokenClaims?): Boolean {
    if (validClaims == null) {
        return false;
    }

    val erVeileder = validClaims.get("NAVident") != null && validClaims.get("NAVident").toString().isNotEmpty()
    val erSystem = validClaims.get("sub") == validClaims.get("oid")

    return erVeileder || erSystem
}

private fun hentValidTokenClaims(ctx: Context, issuerProperties: Map<Rolle, Pair<String, IssuerProperties>>, rolle: Rolle) =
    hentTokenValidationHandler(issuerProperties, rolle)
        .getValidatedTokens(ctx.httpRequest)
        .anyValidClaims

private val Context.httpRequest: HttpRequest
    get() = object : HttpRequest {
        override fun getHeader(headerName: String) = headerMap()[headerName]
    }

