package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import no.nav.security.token.support.core.configuration.IssuerProperties
import java.net.URL

fun main() {
    startEsMock()
    startApp(issuerProperties, ::tomtSikkerhetsfilter)
}

fun startAppForTest() {
    startEsMock()
    startApp(issuerProperties, ::lagSikkerhetsfilter)
}

fun tomtSikkerhetsfilter(javalin: Javalin, issuerProperties: IssuerProperties, tillateUrl: List<String>) {}

private val issuerProperties = IssuerProperties(
            URL("http://localhost:18300/isso-idtoken/.well-known/openid-configuration"),
            listOf("audience"),
"isso-idtoken"
        )
