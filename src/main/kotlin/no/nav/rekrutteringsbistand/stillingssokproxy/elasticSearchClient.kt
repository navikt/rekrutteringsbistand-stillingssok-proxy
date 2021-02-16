package no.nav.rekrutteringsbistand.stillingssokproxy

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.client.RestClient

private val url = environment["ELASTIC_SEARCH_API"]
private val username = environment["ES_USERNAME"]
private val password = environment["ES_PASSWORD"]

val elasticSearchClient: RestClient = RestClient
    .builder(HttpHost.create(url))
    .setRequestConfigCallback {
        it
            .setConnectionRequestTimeout(5000)
            .setConnectTimeout(10000)
            .setSocketTimeout(20000)
    }
    .setHttpClientConfigCallback {
        it.setDefaultCredentialsProvider(credentialsProvider)
    }.build()

private val credentialsProvider: CredentialsProvider = BasicCredentialsProvider().apply {
    setCredentials(
        AuthScope.ANY,
        UsernamePasswordCredentials(username, password)
    )
}