package no.nav.rekrutteringsbistand.stillingssokproxy

import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler
import java.time.LocalDateTime

data class CachedHandler(
    val handler: JwtTokenValidationHandler,
    val expires: LocalDateTime,
)

val cache: HashMap<Rolle, CachedHandler> = HashMap()

fun hentTokenValidationHandler(
    allIssuerProperties: Map<Rolle, IssuerProperties>,
    rolle: Rolle
): JwtTokenValidationHandler {
    val issuerProperties = allIssuerProperties[rolle]!!
    val cachedHandler = cache[rolle]

    return if (cachedHandler != null && cachedHandler.expires.isAfter(LocalDateTime.now())) {
        cachedHandler.handler
    } else {
        val newHandler = JwtTokenValidationHandler(
            MultiIssuerConfiguration(mapOf(issuerProperties.cookieName to issuerProperties))
        )

        cache[rolle] = CachedHandler(newHandler, LocalDateTime.now().plusHours(1));

        newHandler
    }
}