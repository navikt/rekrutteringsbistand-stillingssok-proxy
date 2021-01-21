package no.nav.rekrutteringsbistand.stillingssokproxy

enum class Kjøremiljø(val value: String) {
    DEV_GCP("dev-gcp"),
    PROD_GCP("prod-gcp"),
    TEST("test"),
    LOCAL("local");
}