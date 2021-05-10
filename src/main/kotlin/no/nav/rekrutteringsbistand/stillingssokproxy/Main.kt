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
val aliveUrl = "/internal/isAlive"
val readyUrl = "/internal/isReady"

private val issuer_isso = "isso-idtoken"

fun startApp(
    issuerProperties: IssuerProperties,
    opprettSikkerhetsfilter: (javalin: Javalin, issuerProperties: IssuerProperties, tillateUrl: List<String>) -> Any
) {
    val javalin = Javalin.create {
        it.defaultContentType = "application/json"
    }

    val tillatteUrl = listOf(aliveUrl, readyUrl)
    opprettSikkerhetsfilter(javalin, issuerProperties, tillatteUrl)

    javalin.routes {
        get(aliveUrl) { it.status(200) }
        get(readyUrl) { it.status(200) }
        post("/:indeks/_search") { context ->
            val elasticSearchSvar = søk(context.body(), context.queryParamMap(), context.pathParam("indeks"))
            context
                .status(elasticSearchSvar.statuskode)
                .result(elasticSearchSvar.resultat)
        }
        post("/:indeks/_explain/:dokumentnummer") { context ->
            val elasticSearchSvar = explain(
                context.body(),
                context.queryParamMap(),
                context.pathParam("indeks"),
                context.pathParam("dokumentnummer")
            )
            context
                .status(elasticSearchSvar.statuskode)
                .result(elasticSearchSvar.resultat)
        }
        get("/:indeks/_doc/:dokumentid") { context ->
            val elasticSearchSvar = hentDokument(context.pathParam("dokumentid"), context.pathParam("indeks"))
            context
                .status(elasticSearchSvar.statuskode)
                .result(elasticSearchSvar.resultat)
        }
    }.start(port)

    javalin.exception(Exception::class.java) { e, ctx ->
        log("Main").error("Feil i stillingproxy", e)
    }
}

fun main() {
    try {
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
    } catch (e: Exception) {
        log("main()").error("Noe galt skjedde", e)
        throw e
    }
}
