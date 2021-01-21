package no.nav.rekrutteringsbistand.stillingssokproxy

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient

fun sok(jsonbody: String, params: Map<String, List<String>>) : String {
    val queryMap = params.entries.map { (key, value) -> return@map Pair(key, value[0]) }.toMap()
    val client = getRestHighLevelClient()
    log("SearchClient").info("Har laget ES-klient")

    return "svar"
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