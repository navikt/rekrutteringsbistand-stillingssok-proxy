package no.nav.rekrutteringsbistand.stillingssokproxy

import io.github.cdimascio.dotenv.dotenv
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post
import no.nav.security.token.support.core.configuration.IssuerProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)

fun log(name: String): Logger = LoggerFactory.getLogger(name)

val environment = dotenv { ignoreIfMissing = true }
val port = 8300
val urlBaseInternal = "http://localhost:$port"
val aliveUrl = "/internal/isAlive"
val readyUrl = "/internal/isReady"

private val issuer_isso = "isso-idtoken"

fun startApp(
    issuerProperties: IssuerProperties,
    opprettSikkerhetsfilter: (javalin: Javalin, issuerProperties: IssuerProperties, tillateUrl: List<String>) -> Any
) {
    val javalin = Javalin.create()
    val indeks = "stilling"

    val tillatteUrl = listOf(urlBaseInternal + aliveUrl, urlBaseInternal + readyUrl)
    opprettSikkerhetsfilter(javalin, issuerProperties, tillatteUrl)

    javalin.routes {
        get(aliveUrl) { it.status(200) }
        get(readyUrl) { it.status(200) }
        post("/_search") { context ->
            val sokeResultat = sok(context.body(), context.queryParamMap(), indeks)
            context
                .status(sokeResultat.statuskode)
                .result(sokeResultat.resultat)
                .header("Content-type", "application/json")
        }
    }.start(port)
}

fun main() {
    val issuerProperties = when (environment["NAIS_CLUSTER_NAME"]) {
        "dev-gcp" -> IssuerProperties(
            URL("https://login.microsoftonline.com/NAVQ.onmicrosoft.com/.well-known/openid-configuration"),
            listOf("38e07d31-659d-4595-939a-f18dce3446c5"),
            issuer_isso
        )
        "prod-gcp" -> IssuerProperties(
            URL("https://login.microsoftonline.com/navno.onmicrosoft.com/.well-known/openid-configuration"),
            listOf("9b4e07a3-4f4c-4bab-b866-87f62dff480d"),
            issuer_isso
        )
        else -> throw RuntimeException("Ukjent cluster")
    }
    startApp(issuerProperties, ::lagSikkerhetsfilter)
}