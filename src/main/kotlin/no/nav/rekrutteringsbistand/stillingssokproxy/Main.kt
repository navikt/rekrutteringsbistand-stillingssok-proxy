package no.nav.rekrutteringsbistand.stillingssokproxy

import io.github.cdimascio.dotenv.dotenv
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post
import no.nav.security.token.support.core.configuration.IssuerProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)

fun log(name: String): Logger = LoggerFactory.getLogger(name)

val environment = dotenv { ignoreIfMissing = true }
val port = 8300
val aliveUrl = "/internal/isAlive"
val readyUrl = "/internal/isReady"

fun startApp(
    issuerProperties: List<IssuerProperties>,
    opprettSikkerhetsfilter: (javalin: Javalin, issuerProperties: List<IssuerProperties>, tillateUrl: List<String>) -> Any
) {
    val javalin = Javalin.create { config ->
        config.http.defaultContentType = "application/json"
    }

    val tillatteUrl = listOf(aliveUrl, readyUrl)
    opprettSikkerhetsfilter(javalin, issuerProperties, tillatteUrl)

    javalin.routes {
        get(aliveUrl) { it.status(200) }
        get(readyUrl) { it.status(200) }
        post("/{indeks}/_search") { context ->
            val openSearchSvar = søk(context.body(), context.queryParamMap(), context.pathParam("indeks"))
            context
                .status(openSearchSvar.statuskode)
                .result(openSearchSvar.resultat)
        }
        post("/{indeks}/_explain/{dokumentnummer}") { context ->
            val openSearchSvar = explain(
                context.body(),
                context.queryParamMap(),
                context.pathParam("indeks"),
                context.pathParam("dokumentnummer")
            )
            context
                .status(openSearchSvar.statuskode)
                .result(openSearchSvar.resultat)
        }
        get("/{indeks}/_doc/{dokumentid}") { context ->
            val openSearchSvar = hentDokument(context.pathParam("dokumentid"), context.pathParam("indeks"))
            context
                .status(openSearchSvar.statuskode)
                .result(openSearchSvar.resultat)
        }
    }.start(port)

    javalin.exception(Exception::class.java) { e, _ ->
        log("Main").error(e.toString(), e)
    }
}

fun main() {
    try {
        startApp(hentIssuerProperties(), ::lagSikkerhetsfilter)
    } catch (e: Exception) {
        log("main()").error(e.toString(), e)
        throw e
    }
}
