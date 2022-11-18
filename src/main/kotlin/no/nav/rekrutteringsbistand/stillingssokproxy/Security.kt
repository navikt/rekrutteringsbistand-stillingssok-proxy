package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse
import io.javalin.http.Handler
import io.javalin.security.AccessManager
import io.javalin.security.RouteRole
import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.http.HttpRequest
import no.nav.security.token.support.core.jwt.JwtTokenClaims

enum class Rolle : RouteRole {
    UNPROTECTED,
    VEILEDER_ELLER_SYSTEMBRUKER,
}

fun styrTilgang(issuerProperties: Map<Rolle, IssuerProperties>) =
    AccessManager { handler: Handler, ctx: Context, roller: Set<RouteRole> ->

        val erAutentisert =
            when {
                roller.contains(Rolle.UNPROTECTED) -> true
                roller.contains(Rolle.VEILEDER_ELLER_SYSTEMBRUKER) -> {
                    val claims = hentValidTokenClaims(ctx, issuerProperties, Rolle.VEILEDER_ELLER_SYSTEMBRUKER)

                    autentiserVeilederEllerSystembruker(claims)
                }

                else -> false
            }

        if (erAutentisert) {
            handler.handle(ctx)
        } else {
            throw ForbiddenResponse()
        }
    }

fun autentiserVeilederEllerSystembruker(validClaims: JwtTokenClaims?): Boolean {
    if (validClaims == null) {
        return false;
    }

    val erVeileder = validClaims.get("NAVident") != null && validClaims["NAVident"].toString().isNotEmpty()
    val erSystem = validClaims.get("sub") == validClaims.get("oid")

    return erVeileder || erSystem
}

private fun hentValidTokenClaims(ctx: Context, issuerProperties: Map<Rolle, IssuerProperties>, rolle: Rolle) =
    hentTokenValidationHandler(issuerProperties, rolle)
        .getValidatedTokens(ctx.httpRequest)
        .anyValidClaims.orElseGet { null }

private val Context.httpRequest: HttpRequest
    get() = object : HttpRequest {
        override fun getHeader(headerName: String?) = headerMap()[headerName]
        override fun getCookies() = cookieMap().map { (name, value) ->
            object : HttpRequest.NameValue {
                override fun getName() = name
                override fun getValue() = value
            }
        }.toTypedArray()
    }

