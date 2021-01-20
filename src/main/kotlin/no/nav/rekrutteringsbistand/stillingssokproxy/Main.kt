package no.nav.rekrutteringsbistand.stillingssokproxy

import io.github.cdimascio.dotenv.dotenv
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)

fun log(name: String): Logger = LoggerFactory.getLogger(name)

val environment = dotenv { ignoreIfMissing = true }

fun main() {
    log("main").info("Starter applikasjon")

    val port = environment["JAVALIN_PORT"].toInt()
    val aliveUrl = "http://localhost:${port}/internal/isAlive"
    val readyUrl = "http://localhost:${port}/internal/isReady"

    val javalin = Javalin.create().start(port)

    if (environment["NAIS_CLUSTER_NAME"] == "local") {
        log("main").warn("Applikasjonen settes opp med konfigurasjon for lokal kjøring")
        javalin.before { context ->
            context.cookieStore("innloggetVeileder", Security.InnloggetVeileder("brukernavn", "visningsnavn", "navident"))
        }
    } else {
        val security = Security()
        security.lagSikkerhetsfilter(javalin, listOf(aliveUrl, readyUrl))
    }

    javalin.routes {
        get(aliveUrl) { context ->
            log("Main").info("Sjekker isAlive")
            context.status(200)
        }
        get(readyUrl) { context ->
            log("Main").info("Sjekker isReady")
            context.status(200)
        }
        get("/test") { it.status(200) }
    }
}