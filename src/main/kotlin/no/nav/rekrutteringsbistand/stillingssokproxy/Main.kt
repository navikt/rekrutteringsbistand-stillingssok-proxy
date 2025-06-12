package no.nav.rekrutteringsbistand.stillingssokproxy

import io.github.cdimascio.dotenv.dotenv
import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.micrometer.MicrometerPlugin
import no.nav.rekrutteringsbistand.stillingssokproxy.Singeltons.meterRegistry
import no.nav.security.token.support.core.configuration.IssuerProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)

fun noClassLogger(): Logger {
    val callerClassName = Throwable().stackTrace[1].className
    return LoggerFactory.getLogger(callerClassName)
}

private val log = noClassLogger()

val environment = dotenv { ignoreIfMissing = true }

fun main() {
    try {
        val issuerProperties = hentIssuerProperties(System.getenv())
        val javalin = opprettJavalinMedTilgangskontroll()

        startApp(javalin, issuerProperties)
    } catch (e: Exception) {
        log.error(e.toString(), e)
        throw e
    }
}


fun opprettJavalinMedTilgangskontroll(): Javalin =
    Javalin.create { config ->
        config.registerPlugin(MicrometerPlugin { micrometerConfig ->
            micrometerConfig.registry = meterRegistry
        })
        config.http.defaultContentType = "application/json"
    }.start(8300)

fun startApp(
    javalin: Javalin,
    issuerProperties: Map<Rolle, Pair<String, IssuerProperties>>,
) {
    javalin.apply {
        get("/internal/isAlive", { it.status(200) })
        get("/internal/isReady", { it.status(200) })

        get("/{indeks}/_search", search(issuerProperties))
        post("/{indeks}/_search", search(issuerProperties))
        post("/{indeks}/_explain/{dokumentnummer}", explainDocument(issuerProperties))
        get("/{indeks}/_doc/{dokumentid}", getDocument(issuerProperties))
    }

    javalin.exception(Exception::class.java) { e, _ ->
        log.error(e.toString(), e)
    }
}

val search: (Map<Rolle, Pair<String, IssuerProperties>>) -> (Context) -> Unit = { issuerProps ->
    { context ->
        context.sjekkTilgang(Rolle.VEILEDER_ELLER_SYSTEMBRUKER, issuerProps)
        val openSearchSvar = søk(
            context.body(),
            context.queryParamMap(),
            context.pathParam("indeks")
        )
        context.status(openSearchSvar.statuskode).result(openSearchSvar.resultat)
    }
}

val explainDocument: (Map<Rolle, Pair<String, IssuerProperties>>) -> (Context) -> Unit = { issuerProps ->
    { context ->
        context.sjekkTilgang(Rolle.VEILEDER_ELLER_SYSTEMBRUKER, issuerProps)
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
}

val getDocument: (Map<Rolle, Pair<String, IssuerProperties>>) -> (Context) -> Unit = { issuerProps ->
    { context ->
        context.sjekkTilgang(Rolle.VEILEDER_ELLER_SYSTEMBRUKER, issuerProps)
        val openSearchSvar = hentDokument(
            context.pathParam("dokumentid"),
            context.pathParam("indeks")
        )

        context
            .status(openSearchSvar.statuskode)
            .result(openSearchSvar.resultat)
    }
}
