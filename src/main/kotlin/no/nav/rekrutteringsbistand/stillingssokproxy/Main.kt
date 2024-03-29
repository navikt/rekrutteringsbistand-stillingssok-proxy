package no.nav.rekrutteringsbistand.stillingssokproxy

import io.github.cdimascio.dotenv.dotenv
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.http.Context
import io.javalin.security.RouteRole
import no.nav.security.token.support.core.configuration.IssuerProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)

fun log(name: String): Logger = LoggerFactory.getLogger(name)

val environment = dotenv { ignoreIfMissing = true }

fun main() {
    try {
        val issuerProperties = hentIssuerProperties()
        val javalin = opprettJavalinMedTilgangskontroll(issuerProperties)

        startApp(javalin)
    } catch (e: Exception) {
        log("main()").error(e.toString(), e)
        throw e
    }
}


fun opprettJavalinMedTilgangskontroll(
    issuerProperties: Map<Rolle, IssuerProperties>
): Javalin =
    Javalin.create {config ->
        config.http.defaultContentType = "application/json"
        config.accessManager(styrTilgang(issuerProperties))
    }.start(8300)

fun startApp(
    javalin: Javalin
) {
    javalin.routes {
        get("/internal/isAlive", { it.status(200) }, Rolle.UNPROTECTED)
        get("/internal/isReady", { it.status(200) }, Rolle.UNPROTECTED)

        get("/{indeks}/_search", search, Rolle.VEILEDER_ELLER_SYSTEMBRUKER)
        post("/{indeks}/_search", search, Rolle.VEILEDER_ELLER_SYSTEMBRUKER)
        post("/{indeks}/_explain/{dokumentnummer}", explainDocument, Rolle.VEILEDER_ELLER_SYSTEMBRUKER)
        get("/{indeks}/_doc/{dokumentid}", getDocument, Rolle.VEILEDER_ELLER_SYSTEMBRUKER)
    }

    javalin.exception(Exception::class.java) { e, _ ->
        log("Main").error(e.toString(), e)
    }
}

val search: (Context) -> Unit = { context ->
    val openSearchSvar = søk(
        context.body(),
        context.queryParamMap(),
        context.pathParam("indeks")
    )

    context
        .status(openSearchSvar.statuskode)
        .result(openSearchSvar.resultat)
}

val explainDocument: (Context) -> Unit = { context ->
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

val getDocument: (Context) -> Unit = { context ->
    val openSearchSvar = hentDokument(
        context.pathParam("dokumentid"),
        context.pathParam("indeks")
    )

    context
        .status(openSearchSvar.statuskode)
        .result(openSearchSvar.resultat)
}
