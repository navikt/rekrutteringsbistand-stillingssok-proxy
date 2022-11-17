package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import no.nav.security.token.support.core.configuration.IssuerProperties
import java.net.URL

const val azureAdIssuer = "azure-ad-issuer"
const val ownClientId = "audience"
const val clientIdOfSomeApp = "client-id"

fun main() {
    OsMock.startOsMock()

    val javalin = opprettJavalinMedTilgangskontroll(LokalApplikasjon.issuerProperties)

    startApp(javalin)
}

object LokalApplikasjon {
    private var javalinServerStartet = false
    private lateinit var javalin: Javalin

    fun startAppForTest() {
        OsMock.startOsMock()

        javalin = opprettJavalinMedTilgangskontroll(issuerProperties)

        if (!javalinServerStartet) {
            startApp(javalin)

            javalinServerStartet = true
        }
    }

    fun avsluttAppForTest() {
        javalin.stop()
        javalinServerStartet = false
    }

    val issuerProperties = mapOf(
        Rolle.VEILEDER_ELLER_SYSTEMBRUKER to IssuerProperties(
            URL("http://localhost:18300/$azureAdIssuer/.well-known/openid-configuration"),
            listOf(ownClientId),
            "azuread"
        )
    )
}
