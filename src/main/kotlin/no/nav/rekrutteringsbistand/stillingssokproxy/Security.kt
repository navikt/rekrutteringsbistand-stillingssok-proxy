package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse
import io.javalin.http.Handler
import io.javalin.security.AccessManager
import io.javalin.security.RouteRole
import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.http.HttpRequest
import no.nav.security.token.support.core.jwt.JwtTokenClaims
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler

enum class Rolle : RouteRole {
    UNPROTECTED,
    VEILEDER_ELLER_SYSTEMBRUKER,
}

fun styrTilgang(tokenValidationHandler: JwtTokenValidationHandler) =
    AccessManager { handler: Handler, ctx: Context, roller: Set<RouteRole> ->

        val erAutentisert =
            when {
                roller.contains(Rolle.UNPROTECTED) -> true
                roller.contains(Rolle.VEILEDER_ELLER_SYSTEMBRUKER) -> autentiserVeileder(hentTokenClaims(ctx, tokenValidationHandler))
                else -> false
            }

        if (erAutentisert) {
            handler.handle(ctx)
        } else {
            throw ForbiddenResponse()
        }
    }

fun interface Autentiseringsmetode {
    operator fun invoke(claims: JwtTokenClaims?): Boolean
}

val autentiserVeileder = Autentiseringsmetode { claims ->
    val erVeileder = claims?.get("NAVident") != null && claims["NAVident"].toString().isNotEmpty()
    val erSystem = claims?.get("sub") == claims?.get("oid")

    claims != null && (erVeileder || erSystem)
}

private fun hentTokenClaims(ctx: Context, tokenValidationHandler: JwtTokenValidationHandler) =
    tokenValidationHandler
        .getValidatedTokens(ctx.httpRequest)
        .anyValidClaims.orElseGet { null }

fun lagTokenValidationHandler(issuerProperties: IssuerProperties) =
    JwtTokenValidationHandler(
        MultiIssuerConfiguration(mapOf(issuerProperties.cookieName to issuerProperties))
    )

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

