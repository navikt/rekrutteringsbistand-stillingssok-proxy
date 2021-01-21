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
val port = 8300
val urlBaseInternal = "http://localhost:$port"
val aliveUrl = "/internal/isAlive"
val readyUrl = "/internal/isReady"

fun startApp(kjøremiljø: Kjøremiljø) {
    val javalin = Javalin.create()

    javalin.routes {
        get(aliveUrl) { it.status(200) }
        get(readyUrl) { it.status(200) }
        get("/test") { it.status(200) }
    }

    if (kjøremiljø != Kjøremiljø.LOCAL) {
        val tillatteUrl = listOf(urlBaseInternal + aliveUrl, urlBaseInternal + readyUrl)
        lagSikkerhetsfilter(javalin, kjøremiljø, tillatteUrl)
    }

    javalin.start(port)
}

fun main() {
    val kjøremiljø = Kjøremiljø.valueOf(environment["NAIS_CLUSTER_NAME"])
    startApp(kjøremiljø)
}