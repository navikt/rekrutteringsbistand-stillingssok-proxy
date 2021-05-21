package no.nav.rekrutteringsbistand.stillingssokproxy

import no.nav.security.token.support.core.configuration.IssuerProperties
import java.net.URL

private val issuer_isso = "isso-idtoken"
private val issuer_azuread = "azuread"

fun hentIssuerProperties() =
    when(environment["NAIS_CLUSTER_NAME"]) {
        "dev-gcp" -> listOf(
            IssuerProperties(
                URL("https://login.microsoftonline.com/NAVQ.onmicrosoft.com/.well-known/openid-configuration"),
                listOf("38e07d31-659d-4595-939a-f18dce3446c5"),
                issuer_isso
            ),
            IssuerProperties(
                URL("https://login.microsoftonline.com/NAVQ.onmicrosoft.com/.well-known/openid-configuration"),
                listOf("38e07d31-659d-4595-939a-f18dce3446c5", "ce2dc2d7-403f-432f-8fab-2d589de424ef"),
                issuer_azuread
            )
        )
        "prod-gcp" -> listOf(
            IssuerProperties(
                URL("https://login.microsoftonline.com/navno.onmicrosoft.com/.well-known/openid-configuration"),
                listOf("9b4e07a3-4f4c-4bab-b866-87f62dff480d"),
                issuer_isso
            )
        )
        else -> throw RuntimeException("Ukjent cluster")
    }
