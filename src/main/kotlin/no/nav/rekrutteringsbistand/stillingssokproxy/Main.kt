package no.nav.rekrutteringsbistand.stillingssokproxy

import com.nimbusds.jwt.JWTClaimsSet
import io.github.cdimascio.dotenv.dotenv
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetAddress

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)

fun log(name: String): Logger = LoggerFactory.getLogger(name)

val environment = dotenv { ignoreIfMissing = true }

fun main() {
    log("main").info("Starter applikasjon")

    val port = 8300

    val urlBaseInternal = "http://localhost:$port"
    val aliveUrl = "/internal/isAlive"
    val readyUrl = "/internal/isReady"

    val javalin = Javalin.create().start(port)

    if (environment["NAIS_CLUSTER_NAME"] == "local") {
        log("main").warn("Applikasjonen settes opp med konfigurasjon for lokal kjøring")
        val mockOAuth2Server = MockOAuth2Server()
        mockOAuth2Server.start(InetAddress.getByName("localhost"), 18300)
        val issueToken = mockOAuth2Server.issueToken("isso-idtoken", "someclientid",
            DefaultOAuth2TokenCallback(
                issuerId = "isso-idtoken",
                claims = mapOf(
                    Pair("name", "navn"),
                    Pair("NAVident", "NAVident"),
                    Pair("unique_name", "unique_name"),

                    ),
                audience = listOf("audience")
            ))

        log("Main").info("BaseUrl: ${mockOAuth2Server.baseUrl()}")

        javalin.before { context ->
            log("main").info("*** ${issueToken.serialize()}")
            context.cookie("isso-idtoken", issueToken.serialize())
        }
    }

    val tillatteUrl = listOf(urlBaseInternal + aliveUrl, urlBaseInternal + readyUrl)
    lagSikkerhetsfilter(javalin, tillatteUrl)


    javalin.routes {
        get(aliveUrl) { it.status(200) }
        get(readyUrl) { it.status(200) }
        get("/test") { it.status(200) }
    }
}