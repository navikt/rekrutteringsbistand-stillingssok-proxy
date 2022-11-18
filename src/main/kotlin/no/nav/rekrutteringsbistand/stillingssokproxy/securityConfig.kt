package no.nav.rekrutteringsbistand.stillingssokproxy

import no.nav.security.token.support.core.configuration.IssuerProperties
import java.net.URL

fun hentIssuerProperties() =
    mapOf(
        Rolle.VEILEDER_ELLER_SYSTEMBRUKER to
            IssuerProperties(
                URL(System.getenv("AZURE_APP_WELL_KNOWN_URL")),
                listOf(System.getenv("AZURE_APP_CLIENT_ID")),
                System.getenv("AZURE_OPENID_CONFIG_ISSUER")
            )
    )

