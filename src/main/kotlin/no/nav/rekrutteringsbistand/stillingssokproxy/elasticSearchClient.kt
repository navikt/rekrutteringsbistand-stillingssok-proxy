package no.nav.rekrutteringsbistand.stillingssokproxy

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.client.RestClient

object ElasticSearch {

    val client: RestClient by lazy {
        val username = environment["ES_USERNAME"]
        val password = environment["ES_PASSWORD"]
        val url = environment["ELASTIC_SEARCH_API"]
        RestClient
            .builder(HttpHost.create(url))
            .setRequestConfigCallback {
                it
                    .setConnectionRequestTimeout(5000)
                    .setConnectTimeout(10000)
                    .setSocketTimeout(20000)
            }
            .setHttpClientConfigCallback {
                it.setDefaultCredentialsProvider(
                    BasicCredentialsProvider().apply {
                        setCredentials(
                            AuthScope.ANY,
                            UsernamePasswordCredentials(username, password)
                        )
                    }
                )
            }.build()
    }

}