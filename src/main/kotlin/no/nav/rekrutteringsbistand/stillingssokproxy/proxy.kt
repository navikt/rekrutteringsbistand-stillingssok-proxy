package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.http.InternalServerErrorResponse
import org.apache.hc.client5.http.ClientProtocolException
import org.opensearch.client.opensearch.generic.Request
import org.opensearch.client.opensearch.generic.Requests
import org.opensearch.client.transport.httpclient5.ResponseException
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.jvm.optionals.getOrNull

private val log = noClassLogger()

fun søk(jsonbody: String, params: Map<String, List<String>>, indeks: String): OpenSearchSvar {
    val request = openSearchRequest("POST", "/$indeks/_search", params, jsonbody)
    return gjørRequest(request, "/stilling/_search")
}

fun hentDokument(dokumentnummer: String, indeks: String): OpenSearchSvar {
    val request = Requests.builder().method("GET").endpoint("/$indeks/_doc/$dokumentnummer").build()
    return gjørRequest(request, "/stilling/_doc")
}

fun explain(
    jsonbody: String,
    params: Map<String, List<String>>,
    indeks: String,
    dokumentnummer: String
): OpenSearchSvar {
    val request = openSearchRequest("GET", "/$indeks/_explain/$dokumentnummer", params, jsonbody)
    return gjørRequest(request, "/stilling/_explain")
}

private fun gjørRequest(request: Request, kortUrl: String): OpenSearchSvar {
    val start = System.currentTimeMillis()

    val svar = try {
        OpenSearch.client.execute(request).use { response ->
            OpenSearchSvar(response.status, response.body.getOrNull()?.bodyAsString() ?: "")
        }
    } catch (e: Exception) {
        log.error("Feil ved kall mot OpenSearch. Method: ${request.method}, URI: ${request.endpoint}, Host: ${OpenSearch.host}", e)
        return when (e) {
            is ResponseException -> {
                loggFeilsvar(request, OpenSearchSvar(e.status(), e.message ?: ""))
                OpenSearchSvar(e.status(), "Det skjedde en feil ved kall mot OpenSearch. Se team-logs for detaljer.")
            }
            is ClientProtocolException -> OpenSearchSvar(500, "Proxy har HTTP-protokollfeil mot OpenSearch")
            is IOException -> OpenSearchSvar(504, "Problem med tilkobling til OpenSearch")
            else -> throw InternalServerErrorResponse("Det skjedde en feil ved kall mot OpenSearch. Se team-logs for detaljer.")
        }
    }

    Singeltons.meterRegistry.let { m ->
        val meter = m.timer("outbound_requests", "target", kortUrl, "status", svar.statuskode.toString())
        meter.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)
    }

    if (svar.statuskode !in 200..299) {
        loggFeilsvar(request, svar)
    }
    return svar
}

private fun loggFeilsvar(request: Request, svar: OpenSearchSvar) {
    log.error(
        "Feil ved kall mot OpenSearch. " +
        "Method: ${request.method}, " +
        "URI: ${request.endpoint}, " +
        "Host: ${OpenSearch.host}, " +
        "HTTP Status: ${svar.statuskode}. " +
        "Se team-logs for detaljer."
    )

    val fullLogMessage = "Feil ved kall mot OpenSearch. " +
            "Method: ${request.method}, " +
            "URI: ${request.endpoint}, " +
            "Params: ${request.parameters}, " +
            "Host: ${OpenSearch.host}, " +
            "HTTP Status: ${svar.statuskode}, " +
            "Response: ${svar.resultat}"

    log.error(teamLogsMarker, fullLogMessage)

    request.body.getOrNull()?.let {
        log.error(teamLogsMarker, "Søk som feilet mot opensearch (request body): ${it.bodyAsString()}")
    }
}

private fun openSearchRequest(method: String, endpoint: String, params: Map<String, List<String>>, body: String) =
    Requests.builder()
        .method(method)
        .endpoint(endpoint)
        .query(params.mapValues { (_, values) -> values[0] })
        .json(body)
        .build()

data class OpenSearchSvar(
    val statuskode: Int,
    val resultat: String
)
