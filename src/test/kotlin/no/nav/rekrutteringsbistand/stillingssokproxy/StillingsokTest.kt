package no.nav.rekrutteringsbistand.stillingssokproxy

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.jackson.responseObject
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.InetAddress
import java.nio.charset.Charset

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StillingsokTest {

    val mockOAuth2Server = MockOAuth2Server()
    val searchurl = "http://localhost:8300/_search"

    @BeforeAll
    fun init() {
        startApp(Kjøremiljø.TEST)
        startEsMock()
        mockOAuth2Server.start(InetAddress.getByName("localhost"), 18300)
    }

    @Test
    fun `Kall med autentisert bruker mot beskyttet endepunkt skal returnere 200`() {
        val token = hentToken(mockOAuth2Server)
        val (_, response, result) = Fuel.post(searchurl).authentication()
            .bearer(token.serialize())
            .responseString(Charset.defaultCharset())
        assertThat(response.statusCode).isEqualTo(200)
        assertThat(response.body().asString("application/json")).isEqualTo(jsonResultat)
    }

    private fun hentToken(mockOAuth2Server: MockOAuth2Server) = mockOAuth2Server.issueToken("isso-idtoken", "someclientid",
        DefaultOAuth2TokenCallback(
            issuerId = "isso-idtoken",
            claims = mapOf(
                Pair("name", "navn"),
                Pair("NAVident", "NAVident"),
                Pair("unique_name", "unique_name"),
                ),
            audience = listOf("audience")
        )
    )
}