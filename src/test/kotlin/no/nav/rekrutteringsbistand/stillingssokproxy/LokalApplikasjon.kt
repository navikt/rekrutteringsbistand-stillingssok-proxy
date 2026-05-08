package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import no.nav.security.token.support.core.configuration.IssuerProperties
import java.net.URI

const val azureAdIssuer = "azure-ad-issuer"
const val ownClientId = "audience"
const val clientIdOfSomeApp = "client-id"

fun main() {
    OsMock.startOsMock()
    startApp(LokalApplikasjon.issuerProperties)
}

object LokalApplikasjon {
    private lateinit var javalin: Javalin

    fun startAppForTest() {
        OsMock.startOsMock()
        javalin = startApp(issuerProperties)
    }

    fun avsluttAppForTest() {
        javalin.stop()
    }

    val issuerProperties =
        mapOf(
            Rolle.VEILEDER_ELLER_SYSTEMBRUKER to
                    ("http://localhost:18300/azure-ad-issuer" to
                            IssuerProperties(
                                discoveryUrl = URI("http://localhost:18300/$azureAdIssuer/.well-known/openid-configuration").toURL(),
                                acceptedAudience = listOf(ownClientId)
                            ))
        )
}

