package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.http.InternalServerErrorResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.elasticsearch.client.Request
import org.elasticsearch.client.ResponseException
import java.io.IOException

fun søk(jsonbody: String, params: Map<String, List<String>>, indeks: String): ElasticSearchSvar {
    val request = elasticSearchRequest("GET", "$indeks/_search", params, jsonbody)
    return gjørRequest(request)
}

fun explain(
    jsonbody: String,
    params: Map<String, List<String>>,
    indeks: String,
    dokumentnummer: String
): ElasticSearchSvar {
    val request = elasticSearchRequest("GET", "$indeks/_explain/$dokumentnummer", params, jsonbody)
    return gjørRequest(request)
}

private fun gjørRequest(request: Request): ElasticSearchSvar = try {
    val response = elasticSearchClient.performRequest(request)
    val statusKode = response.statusLine.statusCode
    val resultat = EntityUtils.toString(response.entity)
    ElasticSearchSvar(statusKode, resultat)

} catch (e: Exception) {
    log("SearchClient").error("Feil ved kall mot ElasticSearch", e)

    when (e) {
        is ResponseException -> ElasticSearchSvar(
            e.response.statusLine.statusCode,
            EntityUtils.toString(e.response.entity)
        )
        is ClientProtocolException -> ElasticSearchSvar(500, "Proxy har HTTP-protokollfeil mot ElasticSearch")
        is IOException -> ElasticSearchSvar(504, "Problem med tilkobling til ElasticSearch")
        else -> throw InternalServerErrorResponse()
    }
}

private fun elasticSearchRequest(method: String, endpoint: String, params: Map<String, List<String>>, body: String) =
    Request(method, endpoint).apply {
        params.forEach { (name: String?, values: List<String>) ->
            addParameter(name, values[0])
        }
        entity = StringEntity(body, ContentType.APPLICATION_JSON)
    }

data class ElasticSearchSvar(
    val statuskode: Int,
    val resultat: String
)
