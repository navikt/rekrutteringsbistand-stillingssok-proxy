package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.http.InternalServerErrorResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.opensearch.client.Request
import org.opensearch.client.ResponseException
import java.io.IOException

fun søk(jsonbody: String, params: Map<String, List<String>>, indeks: String): OpenSearchSvar {
    val request = openSearchRequest("GET", "$indeks/_search", params, jsonbody)
    return gjørRequest(request)
}

fun hentDokument(dokumentnummer: String, indeks: String): OpenSearchSvar =
    gjørRequest(Request("GET", "$indeks/_doc/$dokumentnummer"))

fun explain(
    jsonbody: String,
    params: Map<String, List<String>>,
    indeks: String,
    dokumentnummer: String
): OpenSearchSvar {
    val request = openSearchRequest("GET", "$indeks/_explain/$dokumentnummer", params, jsonbody)
    return gjørRequest(request)
}

private fun gjørRequest(request: Request): OpenSearchSvar = try {
    val response = OpenSearch.client.performRequest(request)
    val statusKode = response.statusLine.statusCode
    val resultat = EntityUtils.toString(response.entity)
    OpenSearchSvar(statusKode, resultat)
} catch (e: Exception) {
    log("SearchClient").error("Feil ved kall mot OpenSearch", e)

    when (e) {
        is ResponseException -> OpenSearchSvar(
            e.response.statusLine.statusCode,
            EntityUtils.toString(e.response.entity)
        )

        is ClientProtocolException -> OpenSearchSvar(500, "Proxy har HTTP-protokollfeil mot OpenSearch")
        is IOException -> OpenSearchSvar(504, "Problem med tilkobling til OpenSearch")
        else -> throw InternalServerErrorResponse()
    }
}

private fun openSearchRequest(method: String, endpoint: String, params: Map<String, List<String>>, body: String) =
    Request(method, endpoint).apply {
        params.forEach { (name: String?, values: List<String>) ->
            addParameter(name, values[0])
        }
        entity = StringEntity(body, ContentType.APPLICATION_JSON)
    }

data class OpenSearchSvar(
    val statuskode: Int,
    val resultat: String
)
