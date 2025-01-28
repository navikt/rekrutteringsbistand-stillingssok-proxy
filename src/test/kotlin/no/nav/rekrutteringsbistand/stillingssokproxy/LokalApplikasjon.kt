package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import no.nav.security.token.support.core.configuration.IssuerProperties
import java.net.URL

const val azureAdIssuer = "azure-ad-issuer"
const val ownClientId = "audience"
const val clientIdOfSomeApp = "client-id"

fun main() {
    OsMock.startOsMock()

    val javalin = opprettJavalinMedTilgangskontroll()



    startApp(javalin, LokalApplikasjon.issuerProperties)
}

object LokalApplikasjon {
    private var javalinServerStartet = false
    private lateinit var javalin: Javalin

    fun startAppForTest() {
        OsMock.startOsMock()

        javalin = opprettJavalinMedTilgangskontroll()

        if (!javalinServerStartet) {
            startApp(javalin, issuerProperties)

            javalinServerStartet = true
        }
    }

    fun avsluttAppForTest() {
        javalin.stop()
        javalinServerStartet = false
    }

    val issuerProperties =
        mapOf(
            Rolle.VEILEDER_ELLER_SYSTEMBRUKER to
                    ("http://localhost:18300/azure-ad-issuer" to
                            IssuerProperties(
                                discoveryUrl = URL("http://localhost:18300/$azureAdIssuer/.well-known/openid-configuration"),
                                acceptedAudience = listOf(ownClientId)
                            ))
        )
}

