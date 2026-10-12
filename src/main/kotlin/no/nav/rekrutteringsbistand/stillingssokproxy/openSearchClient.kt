package no.nav.rekrutteringsbistand.stillingssokproxy

import org.apache.hc.client5.http.auth.AuthScope
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.util.Timeout
import org.opensearch.client.opensearch.generic.OpenSearchGenericClient
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder

object OpenSearch {

    val host: HttpHost by lazy { HttpHost.create(environment["OPEN_SEARCH_URI"]) }

    val client: OpenSearchGenericClient by lazy {
        val username = environment["OPEN_SEARCH_USERNAME"]
        val password = environment["OPEN_SEARCH_PASSWORD"]

        val transport = ApacheHttpClient5TransportBuilder
            .builder(host)
            .setConnectionConfigCallback {
                it
                    .setConnectTimeout(Timeout.ofMilliseconds(10000))
                    .setSocketTimeout(Timeout.ofMilliseconds(20000))
            }
            .setRequestConfigCallback {
                it.setConnectionRequestTimeout(Timeout.ofMilliseconds(5000))
            }
            .setHttpClientConfigCallback {
                it.setDefaultCredentialsProvider(
                    BasicCredentialsProvider().apply {
                        setCredentials(
                            AuthScope(host),
                            UsernamePasswordCredentials(username, password?.toCharArray())
                        )
                    }
                )
            }.build()

        OpenSearchGenericClient(transport)
    }

}
