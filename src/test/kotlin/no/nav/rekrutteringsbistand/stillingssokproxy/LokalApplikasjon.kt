package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import no.nav.security.token.support.core.configuration.IssuerProperties
import java.net.URL

const val azureAdIssuer = "azure-ad-issuer"
const val ownClientId = "audience"
const val clientIdOfSomeApp = "client-id"

fun main() {
    OsMock.startOsMock()

    val javalin = opprettJavalinMedTilgangskontroll(issuerProperties)

    startApp(javalin)
}

val issuerProperties = mapOf(
    Rolle.VEILEDER_ELLER_SYSTEMBRUKER to IssuerProperties(
        URL("http://localhost:18300/$azureAdIssuer/.well-known/openid-configuration"),
        listOf(ownClientId),
        "azuread"
    )
)
