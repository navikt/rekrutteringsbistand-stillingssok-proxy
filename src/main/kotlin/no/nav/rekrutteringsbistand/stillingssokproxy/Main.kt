package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)

fun log(name: String): Logger = LoggerFactory.getLogger(name)

fun main() {
    log("main").info("Starter applikasjon")

    val port = (System.getenv("JAVALIN_PORT") ?: "8300").toInt()
    val aliveUrl = "http://localhost:${port}/internal/isAlive"
    val readyUrl = "http://localhost:${port}/internal/isReady"

    val javalin = Javalin.create().start(port)

    val security = Security()
    security.lagSikkerhetsfilter(javalin, listOf(aliveUrl, readyUrl))

    javalin.routes {
        get(aliveUrl) { it.status(200) }
        get(readyUrl) { it.status(200) }
        get("/test") { it.status(200) }
    }
}


/*
Ta i mot request
Bruke JwtTokenRetriever til å hente ut fra header eller cookie
Validere via samme bibliotek og sette til context i app
 */