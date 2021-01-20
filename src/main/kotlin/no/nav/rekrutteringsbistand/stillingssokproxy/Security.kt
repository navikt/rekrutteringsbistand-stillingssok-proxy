package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.Javalin
import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse
import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.http.HttpRequest
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler
import java.net.URL
import javax.servlet.http.Cookie


private val issuer_isso = "isso-idtoken"

fun lagSikkerhetsfilter(javalin: Javalin, tillateUrl: List<String>) {
    javalin.before { context ->
        val url: String = context.req.requestURL.toString()

        val erÅpenUrl = tillateUrl.contains(url)

        if (!erÅpenUrl) {
            try {
                innloggetVeileder(context)
            } catch (e: Exception) {
                throw ForbiddenResponse()
            }
        }
    }
}

private fun innloggetVeileder(context: Context): InnloggetVeileder {
    val tokenValidationHandler = JwtTokenValidationHandler(getMultiIssuerConfiguration())
    val tokenValidationContext = tokenValidationHandler.getValidatedTokens(getHttpRequest(context))

    val claims = tokenValidationContext.getClaims(issuer_isso)

    val innloggetVeileder = InnloggetVeileder(
        userName = claims["unique_name"].toString(),
        displayName = claims["name"].toString(),
        navIdent = claims["NAVident"].toString()
    )
    innloggetVeileder.validate()

    return innloggetVeileder
}

private fun getMultiIssuerConfiguration(): MultiIssuerConfiguration {
    val properties = when (environment["NAIS_CLUSTER_NAME"]) {
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
        "local" -> IssuerProperties(
            URL("http://localhost:18300/isso-idtoken/.well-known/openid-configuration"),
            listOf("audience"),
            issuer_isso
        )
        else -> throw RuntimeException("Ukjent cluster")
    }
    log("temp").info("env: ${environment["NAIS_CLUSTER_NAME"]} props: $properties")

    val multiIssuerConfiguration = MultiIssuerConfiguration(mapOf(Pair(issuer_isso, properties)))
    return multiIssuerConfiguration
}

private fun getHttpRequest(context: Context): HttpRequest = object : HttpRequest {
    override fun getHeader(headerName: String?) = context.headerMap()[headerName]
    override fun getCookies() = context.cookieMap().map { (name, value) ->
        object: HttpRequest.NameValue {
            override fun getName() = name
            override fun getValue() = value
        }}.toTypedArray()
}

data class InnloggetVeileder(
    val userName: String,
    val displayName: String,
    val navIdent: String
) {

    fun validate() {
        if (listOf<String>(userName, displayName, navIdent).any { s -> s.isEmpty() }) {
            throw RuntimeException("Ugyldig token")
        }
    }

}
