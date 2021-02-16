package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.http.InternalServerErrorResponse
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.config.RequestConfig
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.util.EntityUtils
import org.elasticsearch.client.Request
import org.elasticsearch.client.ResponseException
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import java.io.IOException

fun søk(jsonbody: String, params: Map<String, List<String>>, indeks: String): ElasticSearchSvar {
    val request = elasticSearchRequest("GET", "$indeks/_search", params, jsonbody)
    return gjørRequest(request)
}

fun explain(jsonbody: String, params: Map<String, List<String>>, indeks: String, dokumentnummer: String): ElasticSearchSvar {
    val request = elasticSearchRequest("GET", "$indeks/_explain/$dokumentnummer", params, jsonbody)
    return gjørRequest(request)
}

private fun gjørRequest(request: Request): ElasticSearchSvar {
    // TODO: Opprette kun én klient pr. pod
    val client = getRestHighLevelClient()

    return try {
        val response = client.lowLevelClient.performRequest(request)
        val statusKode = response.statusLine.statusCode
        val resultat = EntityUtils.toString(response.entity)
        ElasticSearchSvar(statusKode, resultat)

    } catch (e: Exception) {
        log("SearchClient").error("Feil ved kall mot ElasticSearch", e)

        when (e) {
            is ResponseException -> ElasticSearchSvar(e.response.statusLine.statusCode, EntityUtils.toString(e.response.entity))
            is ClientProtocolException -> ElasticSearchSvar(500, "Proxy har HTTP-protokollfeil mot ElasticSearch")
            is IOException -> ElasticSearchSvar(504, "Problem med tilkobling til ElasticSearch")
            else -> throw InternalServerErrorResponse()
        }

    } finally {
        client.close()
    }
}

private fun elasticSearchRequest(method: String, endpoint: String, params: Map<String, List<String>>, body: String) =
    Request(method, endpoint).apply {
        params.forEach { (name: String?, values: List<String>) ->
            addParameter(name, values[0])
        }
        entity = StringEntity(body, ContentType.APPLICATION_JSON)
    }

fun getRestHighLevelClient(): RestHighLevelClient {
    val url = environment["ELASTIC_SEARCH_API"]
    val username = environment["ES_USERNAME"]
    val password = environment["ES_PASSWORD"]

    val credentialsProvider: CredentialsProvider = BasicCredentialsProvider()
    credentialsProvider.setCredentials(
        AuthScope.ANY,
        UsernamePasswordCredentials(username, password)
    )

    return RestHighLevelClient(
        RestClient
            .builder(HttpHost.create(url))
            .setRequestConfigCallback { requestConfigBuilder: RequestConfig.Builder ->
                requestConfigBuilder
                    .setConnectionRequestTimeout(5000)
                    .setConnectTimeout(10000)
                    .setSocketTimeout(20000)
            }
            .setHttpClientConfigCallback { httpAsyncClientBuilder: HttpAsyncClientBuilder ->
                httpAsyncClientBuilder
                    .setDefaultCredentialsProvider(credentialsProvider)
            }
    )
}

data class ElasticSearchSvar(
    val statuskode: Int,
    val resultat: String
)
