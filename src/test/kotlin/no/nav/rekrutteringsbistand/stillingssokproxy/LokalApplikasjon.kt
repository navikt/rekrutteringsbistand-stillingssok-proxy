package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import no.nav.security.token.support.core.configuration.IssuerProperties
import java.net.URL

const val azureAdIssuer = "azure-ad-issuer"
const val ownClientId = "audience"
const val clientIdOfSomeApp = "client-id"

fun main() {
    EsMock.startEsMock()
    startApp(listOf(LokalApplikasjon.issuerProperties), LokalApplikasjon::tomtSikkerhetsfilter)
}

object LokalApplikasjon {
    private var javalinServerStartet = false

    fun startAppForTest() {
        EsMock.startEsMock()

        if (!javalinServerStartet) {
            startApp(listOf(issuerProperties), ::lagSikkerhetsfilter)
            javalinServerStartet = true
        }
    }

    fun tomtSikkerhetsfilter(javalin: Javalin, issuerProperties: List<IssuerProperties>, tillateUrl: List<String>) {}

    val issuerProperties = IssuerProperties(
        URL("http://localhost:18300/$azureAdIssuer/.well-known/openid-configuration"),
        listOf(ownClientId),
        "azuread"
    )
}
