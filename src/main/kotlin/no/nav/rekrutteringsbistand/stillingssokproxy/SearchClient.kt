package no.nav.rekrutteringsbistand.stillingssokproxy

import io.javalin.http.InternalServerErrorResponse
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.config.RequestConfig
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.util.EntityUtils
import org.elasticsearch.client.Request
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient

fun sok(jsonbody: String, params: Map<String, List<String>>, indeks: String): String {
    val client = getRestHighLevelClient()
    log("SearchClient").info("Har laget ES-klient")
    val request = elasticSearchRequest("GET", "$indeks/_search", params, jsonbody)

    try {
        val responseEntity = client.lowLevelClient.performRequest(request).entity;
        return EntityUtils.toString(responseEntity)
    } catch (e: Exception) {
        log("SearchClient").error("Kan ikke gjøre kall mot ElasticSearch", e)
        throw InternalServerErrorResponse()
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