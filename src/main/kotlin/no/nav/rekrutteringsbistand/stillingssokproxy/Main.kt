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

    val port = System.getenv("JAVALIN_PORT").toInt()

    val javalin = Javalin.create().start(7000)

    javalin.routes {
        get("/internal/isAlive") { it.status(200) }
        get("/internal/isReady") { it.status(200) }
    }
}