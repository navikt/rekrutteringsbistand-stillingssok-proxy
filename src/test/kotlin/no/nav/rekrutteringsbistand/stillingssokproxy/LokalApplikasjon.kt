package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import no.nav.security.token.support.core.configuration.IssuerProperties
import java.net.URL

fun main() {
    EsMock.startEsMock()
    startApp(LokalApplikasjon.issuerProperties, LokalApplikasjon::tomtSikkerhetsfilter)
}

object LokalApplikasjon {

    private var javalinServerStartet = false

    fun startAppForTest() {
        EsMock.startEsMock()

        if (!javalinServerStartet) {
            startApp(issuerProperties, ::lagSikkerhetsfilter)
            javalinServerStartet = true
        }
    }

    fun tomtSikkerhetsfilter(javalin: Javalin, issuerProperties: IssuerProperties, tillateUrl: List<String>) {}

    val issuerProperties = IssuerProperties(
        URL("http://localhost:18300/isso-idtoken/.well-known/openid-configuration"),
        listOf("audience"),
        "isso-idtoken"
    )
}
