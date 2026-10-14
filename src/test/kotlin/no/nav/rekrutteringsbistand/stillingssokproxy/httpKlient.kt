package no.nav.rekrutteringsbistand.stillingssokproxy

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers

private val httpClient = HttpClient.newHttpClient()

fun httpGet(url: String, bearerToken: String? = null): HttpResponse<String> =
    send(HttpRequest.newBuilder(URI(url)).GET(), bearerToken)

fun httpPost(url: String, bearerToken: String? = null): HttpResponse<String> =
    send(HttpRequest.newBuilder(URI(url)).POST(BodyPublishers.noBody()), bearerToken)

private fun send(request: HttpRequest.Builder, bearerToken: String?): HttpResponse<String> {
    bearerToken?.let { request.header("Authorization", "Bearer $it") }
    return httpClient.send(request.build(), BodyHandlers.ofString())
}
